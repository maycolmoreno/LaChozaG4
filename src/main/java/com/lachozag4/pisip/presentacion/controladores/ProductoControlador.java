
package com.lachozag4.pisip.presentacion.controladores;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IProductoUseCase;
import com.lachozag4.pisip.presentacion.dto.request.ProductoRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.ProductoResponseDTO;
import com.lachozag4.pisip.presentacion.mapeadores.IProductoDtoMapper;
import com.lachozag4.pisip.presentacion.mapeadores.ProductoRequestMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productos")
// @CrossOrigin(origins = "*") // si necesitas probar desde el navegador
@RequiredArgsConstructor // elimina el constructor manual
public class ProductoControlador {

	private final IProductoUseCase productoUseCase;
	private final IProductoDtoMapper mapper;
	private final ProductoRequestMapper requestMapper;

	@PostMapping
	public ResponseEntity<ProductoResponseDTO> crear(@Valid @RequestBody ProductoRequestDTO request) {
		var producto = requestMapper.toDomain(request);
		var creado = productoUseCase.crear(producto, requestMapper.getCategoriaId(request));
		var body = mapper.toResponseDTO(creado);

		return ResponseEntity.status(201).body(body);
	}

	@GetMapping
	public ResponseEntity<List<ProductoResponseDTO>> listar() {
		List<ProductoResponseDTO> lista = productoUseCase.listarTodos().stream()
				.map(mapper::toResponseDTO)
				.toList();
		return ResponseEntity.ok(lista);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductoResponseDTO> obtenerPorId(@PathVariable("id") int id) {
		var producto = productoUseCase.buscarPorId(id);
		return ResponseEntity.ok(mapper.toResponseDTO(producto));
	}

	@GetMapping("/categoria/{idCategoria}")
	public ResponseEntity<List<ProductoResponseDTO>> listarPorCategoria(
			@PathVariable("idCategoria") int idCategoria) {
		List<ProductoResponseDTO> lista = productoUseCase.listarPorCategoria(idCategoria).stream()
				.map(mapper::toResponseDTO)
				.toList();
		return ResponseEntity.ok(lista);
	}

	@GetMapping("/activos")
	public ResponseEntity<List<ProductoResponseDTO>> listarActivos() {
		List<ProductoResponseDTO> lista = productoUseCase.listarActivos().stream()
				.map(mapper::toResponseDTO)
				.toList();
		return ResponseEntity.ok(lista);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProductoResponseDTO> actualizar(@PathVariable("id") int id,
			@Valid @RequestBody ProductoRequestDTO request) {
		var producto = requestMapper.toDomain(request);
		var actualizado = productoUseCase.actualizar(id, producto, requestMapper.getCategoriaId(request));
		return ResponseEntity.ok(mapper.toResponseDTO(actualizado));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") int id) {
		productoUseCase.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
