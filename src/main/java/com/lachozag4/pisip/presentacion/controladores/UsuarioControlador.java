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

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IUsuarioUseCase;
import com.lachozag4.pisip.presentacion.dto.request.UsuarioRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.UsuarioResponseDTO;
import com.lachozag4.pisip.presentacion.mapeadores.IUsuarioDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/usuario")
public class UsuarioControlador {
	private final IUsuarioUseCase usuarioUseCase;
	private final IUsuarioDtoMapper mapper;

	public UsuarioControlador(IUsuarioUseCase usuarioUseCase, IUsuarioDtoMapper mapper) {
		this.usuarioUseCase = usuarioUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)

	public UsuarioResponseDTO crear(@Valid @RequestBody UsuarioRequestDTO request) {
		return mapper.toResponseDTO(usuarioUseCase.crear(mapper.toDomain(request)));

	}
	@GetMapping
	public List<UsuarioResponseDTO> listar() {
		return usuarioUseCase.listar().stream().map(mapper::toResponseDTO).toList();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable int id) {
		usuarioUseCase.eliminar(id);
		return ResponseEntity.noContent().build();
	}

}
