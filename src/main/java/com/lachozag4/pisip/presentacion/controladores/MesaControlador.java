package com.lachozag4.pisip.presentacion.controladores;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IMesaUseCase;
import com.lachozag4.pisip.presentacion.dto.request.MesaRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.MesaResponseDTO;
import com.lachozag4.pisip.presentacion.mapeadores.IMesaDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/mesa")
public class MesaControlador {

	private final IMesaUseCase mesaUseCase;
	private final IMesaDtoMapper mapper;

	public MesaControlador(IMesaUseCase mesaUseCase, IMesaDtoMapper mapper) {
		this.mesaUseCase = mesaUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)

	public MesaResponseDTO crear(@Valid @RequestBody MesaRequestDTO request) {
		return mapper.toResponseDTO(mesaUseCase.crear(mapper.toDomain(request)));

	}
	@GetMapping
	public List<MesaResponseDTO> listar() {
		return mesaUseCase.listar().stream().map(mapper::toResponseDTO).toList();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable int id) {
		mesaUseCase.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
