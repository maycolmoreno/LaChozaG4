package com.lachozag4.pisip.presentacion.controladores;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IComedorUseCase;
import com.lachozag4.pisip.presentacion.dto.request.ComedorRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.ComedorResponseDTO;
import com.lachozag4.pisip.presentacion.mapeadores.IComedorDtoMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comedores")
@RequiredArgsConstructor
public class ComedorControlador {

	private final IComedorUseCase comedorUseCase;
	private final IComedorDtoMapper mapper;

	@PostMapping
	public ResponseEntity<ComedorResponseDTO> crear(@Valid @RequestBody ComedorRequestDTO request) {
		var comedor = comedorUseCase.crear(mapper.toDomain(request));
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponseDTO(comedor));
	}

	@GetMapping
	public ResponseEntity<List<ComedorResponseDTO>> listar() {
		var lista = comedorUseCase.listarTodos().stream().map(mapper::toResponseDTO).toList();
		return ResponseEntity.ok(lista);
	}

	@GetMapping("/activos")
	public ResponseEntity<List<ComedorResponseDTO>> listarActivos() {
		var lista = comedorUseCase.listarActivos().stream().map(mapper::toResponseDTO).toList();
		return ResponseEntity.ok(lista);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ComedorResponseDTO> obtenerPorId(@PathVariable("id") int idcomedor) {
		return ResponseEntity.ok(mapper.toResponseDTO(comedorUseCase.buscarPorId(idcomedor)));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ComedorResponseDTO> actualizar(@PathVariable("id") int idcomedor,
			@Valid @RequestBody ComedorRequestDTO request) {
		var dominio = mapper.toDomain(request);
		var actualizado = comedorUseCase.actualizar(idcomedor, dominio);
		return ResponseEntity.ok(mapper.toResponseDTO(actualizado));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") int idcomedor) {
		comedorUseCase.eliminar(idcomedor);
		return ResponseEntity.noContent().build();
	}
}
