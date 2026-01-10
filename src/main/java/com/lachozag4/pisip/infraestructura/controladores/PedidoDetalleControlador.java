package com.lachozag4.pisip.infraestructura.controladores;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IPedidoDetalleUseCase;
import com.lachozag4.pisip.presentacion.dto.request.PedidoDetalleRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.PedidoDetalleResponseDTO;
import com.lachozag4.pisip.presentacion.mapeadores.IPedidoDetalleDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/pedidodetalle")
public class PedidoDetalleControlador {

	private final IPedidoDetalleUseCase pedidodetalleUseCase;
	private final IPedidoDetalleDtoMapper mapper;

	public PedidoDetalleControlador(IPedidoDetalleUseCase pedidodetalleUseCase, IPedidoDetalleDtoMapper mapper) {
		this.pedidodetalleUseCase = pedidodetalleUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)

	public PedidoDetalleResponseDTO crear(@Valid @RequestBody PedidoDetalleRequestDTO request) {
		return mapper.toResponseDTO(pedidodetalleUseCase.crear(mapper.toDomain(request)));

	}

	public List<PedidoDetalleResponseDTO> listar() {
		return pedidodetalleUseCase.listar().stream().map(mapper::toResponseDTO).toList();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable int id) {
		pedidodetalleUseCase.eliminar(id);
		return ResponseEntity.noContent().build();
	}

}
