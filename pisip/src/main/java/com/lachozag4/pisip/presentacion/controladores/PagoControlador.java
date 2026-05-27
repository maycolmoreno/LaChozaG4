package com.lachozag4.pisip.presentacion.controladores;

import java.net.URI;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IPagoUseCase;
import com.lachozag4.pisip.aplicacion.servicios.ComprobanteService;
import com.lachozag4.pisip.infraestructura.seguridad.Roles;
import com.lachozag4.pisip.presentacion.dto.request.PagoRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.ComprobanteResponseDTO;
import com.lachozag4.pisip.presentacion.dto.response.PagoResponseDTO;
import com.lachozag4.pisip.presentacion.dto.response.SaldoCuentaResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/cuentas/{idCuenta:\\d+}/pagos", produces = "application/json")
@RequiredArgsConstructor
public class PagoControlador {

	private final IPagoUseCase      pagoUseCase;
	private final ComprobanteService comprobanteService;

	@PostMapping(consumes = "application/json")
	@PreAuthorize(Roles.ADMIN_CAJERO)
	public ResponseEntity<PagoResponseDTO> registrarPago(@PathVariable("idCuenta") int idcuenta,
			@Valid @RequestBody PagoRequestDTO request) {
		var pago = pagoUseCase.registrarPago(idcuenta, request.getMonto(), request.getMetodo(), request.getReferencia(),
				request.getUsuario());
		var response = toResponse(pago);
		return ResponseEntity.created(URI.create("/api/cuentas/" + idcuenta + "/pagos/" + response.getIdpago()))
				.body(response);
	}

	@GetMapping
	@PreAuthorize(Roles.ADMIN_CAJERO)
	public ResponseEntity<List<PagoResponseDTO>> listarPorCuenta(@PathVariable("idCuenta") int idcuenta) {
		var lista = pagoUseCase.listarPorCuenta(idcuenta).stream().map(this::toResponse).toList();
		return ResponseEntity.ok(lista);
	}

	@GetMapping("/resumen")
	@PreAuthorize(Roles.ADMIN_CAJERO)
	public ResponseEntity<SaldoCuentaResponseDTO> obtenerResumenCuenta(@PathVariable("idCuenta") int idcuenta) {
		SaldoCuentaResponseDTO dto = new SaldoCuentaResponseDTO();
		dto.setIdcuenta(idcuenta);
		dto.setTotalPagado(pagoUseCase.totalPagadoCuenta(idcuenta));
		dto.setSaldoPendiente(pagoUseCase.saldoPendienteCuenta(idcuenta));
		return ResponseEntity.ok(dto);
	}

	private PagoResponseDTO toResponse(com.lachozag4.pisip.dominio.entidades.Pago pago) {
		PagoResponseDTO dto = new PagoResponseDTO();
		dto.setIdpago(pago.getIdpago());
		dto.setFecha(pago.getFecha());
		dto.setMonto(pago.getMonto());
		dto.setMetodo(pago.getMetodo());
		dto.setReferencia(pago.getReferencia());
		dto.setUsuario(pago.getUsuario());
		dto.setIdcuenta(pago.getIdcuenta());
		dto.setIdcaja(pago.getIdcaja());
		dto.setTotalPagadoCuenta(pagoUseCase.totalPagadoCuenta(pago.getIdcuenta()));
		dto.setSaldoPendienteCuenta(pagoUseCase.saldoPendienteCuenta(pago.getIdcuenta()));
		return dto;
	}

	// ─── Comprobantes ────────────────────────────────────────────────────────────

	/**
	 * POST /api/cuentas/{idCuenta}/pagos/{idPago}/comprobante
	 * Adjunta la imagen del comprobante de transferencia al pago indicado.
	 */
	@PostMapping(value = "/{idPago:\\d+}/comprobante", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize(Roles.ADMIN_CAJERO)
	public ResponseEntity<ComprobanteResponseDTO> subirComprobante(
			@PathVariable("idCuenta") int idcuenta,
			@PathVariable("idPago")   int idpago,
			@RequestParam("archivo")  MultipartFile archivo,
			@RequestParam("usuario")  String usuario) {

		var comp = comprobanteService.subirComprobante(idpago, archivo, usuario);
		var dto  = toComprobanteResponse(comp);
		return ResponseEntity
				.created(URI.create("/api/cuentas/" + idcuenta + "/pagos/" + idpago + "/comprobante"))
				.body(dto);
	}

	/**
	 * GET /api/cuentas/{idCuenta}/pagos/{idPago}/comprobante
	 * Devuelve los metadatos del comprobante incluyendo la URL pública de Dropbox.
	 */
	@GetMapping("/{idPago:\\d+}/comprobante")
	@PreAuthorize(Roles.ADMIN_CAJERO)
	public ResponseEntity<ComprobanteResponseDTO> obtenerComprobante(
			@PathVariable("idCuenta") int idcuenta,
			@PathVariable("idPago")   int idpago) {

		var comp = comprobanteService.obtenerPorPago(idpago);
		return ResponseEntity.ok(toComprobanteResponse(comp));
	}

	/**
	 * DELETE /api/cuentas/{idCuenta}/pagos/{idPago}/comprobante
	 * Elimina el comprobante de Dropbox y de la base de datos.
	 * Solo ADMIN puede eliminar comprobantes ya registrados.
	 */
	@DeleteMapping("/{idPago:\\d+}/comprobante")
	@PreAuthorize(Roles.SOLO_ADMIN)
	public ResponseEntity<Void> eliminarComprobante(
			@PathVariable("idCuenta") int idcuenta,
			@PathVariable("idPago")   int idpago) {

		comprobanteService.eliminarComprobante(idpago);
		return ResponseEntity.noContent().build();
	}

	private ComprobanteResponseDTO toComprobanteResponse(com.lachozag4.pisip.dominio.entidades.ComprobantePago c) {
		ComprobanteResponseDTO dto = new ComprobanteResponseDTO();
		dto.setIdcomprobante(c.getIdcomprobante());
		dto.setIdpago(c.getIdpago());
		dto.setNombreArchivo(c.getNombreArchivo());
		// La URL que recibe MAUI es la URL directa de Dropbox para visualizar la imagen
		dto.setUrlDescarga(c.getUrlDropbox() != null ? c.getUrlDropbox() : "");
		dto.setContentType(c.getContentType());
		dto.setTamano(c.getTamano());
		dto.setUsuarioRegistro(c.getUsuarioRegistro());
		dto.setFechaSubida(c.getFechaSubida());
		return dto;
	}
}
