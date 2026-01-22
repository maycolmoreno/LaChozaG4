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

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IProductoUseCase;
import com.lachozag4.pisip.presentacion.dto.request.ProductoRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.ProductoResponseDTO;
import com.lachozag4.pisip.presentacion.mapeadores.IProductoDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/producto")

public class ProductoControlador {
	private final IProductoUseCase productoUseCase;
	private final IProductoDtoMapper mapper;
	public ProductoControlador(IProductoUseCase usuarioUseCase, IProductoDtoMapper mapper) {
		this.productoUseCase = usuarioUseCase;
		this.mapper = mapper;
	}
	@PostMapping
	@ResponseStatus(value = HttpStatus.CREATED)
	public ProductoResponseDTO crear(@Valid @RequestBody ProductoRequestDTO request) {
		return mapper.toResponseDTO(productoUseCase.crear(mapper.toDomain(request)));

	}
	@GetMapping
	public List<ProductoResponseDTO> listar() {
		return productoUseCase.listar().stream().map(mapper::toResponseDTO).toList();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable int id) {
		productoUseCase.eliminar(id);
		return ResponseEntity.noContent().build();
	}
	

}
