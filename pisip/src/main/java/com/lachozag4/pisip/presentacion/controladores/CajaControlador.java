package com.lachozag4.pisip.presentacion.controladores;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.ICajaUseCase;
import com.lachozag4.pisip.presentacion.dto.request.AperturaCajaRequestDTO;
import com.lachozag4.pisip.presentacion.dto.request.CierreCajaRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.CajaTurnoResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/caja", produces = "application/json")
@RequiredArgsConstructor
public class CajaControlador {

	private final ICajaUseCase cajaUseCase;

	@PostMapping(value = "/apertura", consumes = "application/json")
	public ResponseEntity<CajaTurnoResponseDTO> abrirCaja(@Valid @RequestBody AperturaCajaRequestDTO request) {
		var caja = cajaUseCase.abrirCaja(request.getMontoInicial(), request.getUsuarioApertura(), request.getObservaciones());
		var response = toResponse(caja);
		return ResponseEntity.created(URI.create("/api/caja/" + response.getIdcaja())).body(response);
	}

	@GetMapping("/abierta")
	public ResponseEntity<CajaTurnoResponseDTO> obtenerCajaAbierta() {
		var caja = cajaUseCase.obtenerCajaAbierta();
		return ResponseEntity.ok(toResponse(caja));
	}

	@PostMapping(value = "/cierre", consumes = "application/json")
	public ResponseEntity<CajaTurnoResponseDTO> cerrarCaja(@Valid @RequestBody CierreCajaRequestDTO request) {
		var caja = cajaUseCase.cerrarCaja(request.getMontoDeclaradoCierre(), request.getUsuarioCierre(),
				request.getObservaciones());
		return ResponseEntity.ok(toResponse(caja));
	}

	@GetMapping
	public ResponseEntity<List<CajaTurnoResponseDTO>> listar() {
		var lista = cajaUseCase.listar().stream().map(this::toResponse).toList();
		return ResponseEntity.ok(lista);
	}

	private CajaTurnoResponseDTO toResponse(com.lachozag4.pisip.dominio.entidades.CajaTurno caja) {
		CajaTurnoResponseDTO dto = new CajaTurnoResponseDTO();
		dto.setIdcaja(caja.getIdcaja());
		dto.setFechaApertura(caja.getFechaApertura());
		dto.setFechaCierre(caja.getFechaCierre());
		dto.setMontoInicial(caja.getMontoInicial());
		dto.setMontoEsperadoCierre(caja.getMontoEsperadoCierre());
		dto.setMontoDeclaradoCierre(caja.getMontoDeclaradoCierre());
		dto.setDiferencia(caja.getDiferencia());
		dto.setEstado(caja.getEstado());
		dto.setUsuarioApertura(caja.getUsuarioApertura());
		dto.setUsuarioCierre(caja.getUsuarioCierre());
		dto.setObservaciones(caja.getObservaciones());
		return dto;
	}
}
