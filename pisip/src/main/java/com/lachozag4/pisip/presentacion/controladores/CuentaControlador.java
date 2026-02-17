package com.lachozag4.pisip.presentacion.controladores;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.ICuentaUseCase;
import com.lachozag4.pisip.presentacion.dto.request.CambiarEstadoRequestDTO;
import com.lachozag4.pisip.presentacion.dto.request.CuentaRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.CuentaResponseDTO;
import com.lachozag4.pisip.presentacion.mapeadores.CuentaRequestMapper;
import com.lachozag4.pisip.presentacion.mapeadores.ICuentaDtoMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/cuentas", produces = "application/json")
@RequiredArgsConstructor
public class CuentaControlador {

	private final ICuentaUseCase cuentaUseCase;
	private final ICuentaDtoMapper responseMapper;
	private final CuentaRequestMapper requestMapper;

	@GetMapping
	public ResponseEntity<List<CuentaResponseDTO>> listarTodas() {
		var lista = cuentaUseCase.listar().stream().map(responseMapper::toResponseDTO).toList();
		return ResponseEntity.ok(lista);
	}

	@GetMapping("/abiertas")
	public ResponseEntity<List<CuentaResponseDTO>> listarAbiertas() {
		var lista = cuentaUseCase.listarAbiertas().stream().map(responseMapper::toResponseDTO).toList();
		return ResponseEntity.ok(lista);
	}

	@GetMapping("/{id:\\d+}")
	public ResponseEntity<CuentaResponseDTO> obtenerPorId(@PathVariable("id") int idcuenta) {
		var cuenta = cuentaUseCase.obtenerPorId(idcuenta);
		return ResponseEntity.ok(responseMapper.toResponseDTO(cuenta));
	}

	@PostMapping(consumes = "application/json")
	public ResponseEntity<CuentaResponseDTO> crear(@Valid @RequestBody CuentaRequestDTO request) {
		var dominio = requestMapper.toDomain(request);
		var creada = cuentaUseCase.crear(dominio);
		var body = responseMapper.toResponseDTO(creada);
		return ResponseEntity.created(URI.create("/api/cuentas/" + body.getIdcuenta())).body(body);
	}

	@PatchMapping(value = "/{id:\\d+}/estado", consumes = "application/json")
	public ResponseEntity<CuentaResponseDTO> cambiarEstado(@PathVariable("id") int idcuenta,
			@Valid @RequestBody CambiarEstadoRequestDTO request) {
		var actualizada = cuentaUseCase.cambiarEstado(idcuenta, request.getEstado());
		return ResponseEntity.ok(responseMapper.toResponseDTO(actualizada));
	}

	// Soporte adicional para PUT /{id}/estado (algunos clientes usan PUT en lugar de PATCH)
	@org.springframework.web.bind.annotation.PutMapping(value = "/{id:\\d+}/estado", consumes = "application/json")
	public ResponseEntity<CuentaResponseDTO> cambiarEstadoPut(@PathVariable("id") int idcuenta,
			@Valid @RequestBody CambiarEstadoRequestDTO request) {
		var actualizada = cuentaUseCase.cambiarEstado(idcuenta, request.getEstado());
		return ResponseEntity.ok(responseMapper.toResponseDTO(actualizada));
	}

	@PostMapping("/{idCuenta:\\d+}/pedidos/{idPedido:\\d+}")
	public ResponseEntity<CuentaResponseDTO> agregarPedido(@PathVariable("idCuenta") int idcuenta,
			@PathVariable("idPedido") int idpedido) {
		var actualizada = cuentaUseCase.agregarPedido(idcuenta, idpedido);
		return ResponseEntity.ok(responseMapper.toResponseDTO(actualizada));
	}
}
