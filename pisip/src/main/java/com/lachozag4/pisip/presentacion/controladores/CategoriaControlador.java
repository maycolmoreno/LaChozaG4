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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.ICategoriaUseCase;
import com.lachozag4.pisip.presentacion.dto.request.CategoriaRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.CategoriaResponseDTO;
import com.lachozag4.pisip.presentacion.mapeadores.ICategoriaDtoMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaControlador {

	private final ICategoriaUseCase categoriaUseCase;
	private final ICategoriaDtoMapper mapper;

	@PostMapping
	public ResponseEntity<CategoriaResponseDTO> crear(@Valid @RequestBody CategoriaRequestDTO request) {

		var categoria = categoriaUseCase.crear(mapper.toDomain(request));
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponseDTO(categoria));
	}

	@GetMapping
	public ResponseEntity<List<CategoriaResponseDTO>> listar() {
		var lista = categoriaUseCase.listarTodos().stream().map(mapper::toResponseDTO).toList();
		return ResponseEntity.ok(lista);
	}

	@GetMapping("/activas")
	public ResponseEntity<List<CategoriaResponseDTO>> listarActivas() {
		var lista = categoriaUseCase.listarActivas().stream().map(mapper::toResponseDTO).toList();
		return ResponseEntity.ok(lista);
	}

	@GetMapping("/{id}")
	public ResponseEntity<CategoriaResponseDTO> obtenerPorId(@PathVariable("id") int idcategoria) {
		return ResponseEntity.ok(mapper.toResponseDTO(categoriaUseCase.buscarPorId(idcategoria)));
	}

	@PutMapping("/{id}")
	public ResponseEntity<CategoriaResponseDTO> actualizar(@PathVariable("id") int idcategoria,
			@Valid @RequestBody CategoriaRequestDTO request) {
		var dominio = mapper.toDomain(request);
		var actualizada = categoriaUseCase.actualizar(idcategoria, dominio);
		return ResponseEntity.ok(mapper.toResponseDTO(actualizada));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") int idcategoria) {
		categoriaUseCase.eliminar(idcategoria);
		return ResponseEntity.noContent().build();
	}
}
