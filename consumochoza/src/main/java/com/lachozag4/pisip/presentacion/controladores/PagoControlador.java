package com.lachozag4.pisip.presentacion.controladores;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IPagoUseCase;
import com.lachozag4.pisip.presentacion.dto.request.PagoRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.PagoResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/cuentas/{idCuenta:\\d+}/pagos", produces = "application/json")
@RequiredArgsConstructor
public class PagoControlador {

	private final IPagoUseCase pagoUseCase;

	@PostMapping(consumes = "application/json")
	public ResponseEntity<PagoResponseDTO> registrarPago(@PathVariable("idCuenta") int idcuenta,
			@Valid @RequestBody PagoRequestDTO request) {
		var pago = pagoUseCase.registrarPago(idcuenta, request.getMonto(), request.getMetodo(), request.getReferencia(),
				request.getUsuario());
		var response = toResponse(pago);
		return ResponseEntity.created(URI.create("/api/cuentas/" + idcuenta + "/pagos/" + response.getIdpago()))
				.body(response);
	}

	@GetMapping
	public ResponseEntity<List<PagoResponseDTO>> listarPorCuenta(@PathVariable("idCuenta") int idcuenta) {
		var lista = pagoUseCase.listarPorCuenta(idcuenta).stream().map(this::toResponse).toList();
		return ResponseEntity.ok(lista);
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
}
