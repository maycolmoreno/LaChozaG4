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

import com.lachozag4.pisip.aplicacion.casosuso.entradas.ICategoriaUseCase;
import com.lachozag4.pisip.presentacion.dto.request.CategoriaRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.CategoriaResponseDTO;
import com.lachozag4.pisip.presentacion.mapeadores.ICategoriaDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/categoria")
public class CategoriaControlador {
	private final ICategoriaUseCase categoriaUseCase;
	private final ICategoriaDtoMapper mapper;

	public CategoriaControlador(ICategoriaUseCase categoriaUseCase, ICategoriaDtoMapper mapper) {
		this.categoriaUseCase = categoriaUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)

	public CategoriaResponseDTO crear(@Valid @RequestBody CategoriaRequestDTO request) {
		return mapper.toResponseDTO(categoriaUseCase.crear(mapper.toDomain(request)));

	}
	@GetMapping
	public List<CategoriaResponseDTO> listar() {
		return categoriaUseCase.listar().stream().map(mapper::toResponseDTO).toList();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable int id) {
		categoriaUseCase.eliminar(id);
		return ResponseEntity.noContent().build();
	}

}
