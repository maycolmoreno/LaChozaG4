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

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IClienteUseCase;
import com.lachozag4.pisip.presentacion.dto.request.ClienteRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.ClienteResponseDTO;
import com.lachozag4.pisip.presentacion.mapeadores.IClienteDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/cliente")
public class ClienteControlador {
	private final IClienteUseCase clienteUseCase;
	private final IClienteDtoMapper mapper;

	public ClienteControlador(IClienteUseCase clienteUseCase, IClienteDtoMapper mapper) {
		this.clienteUseCase = clienteUseCase;
		this.mapper = mapper;
	}
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)

	public ClienteResponseDTO crear(@Valid @RequestBody ClienteRequestDTO request) {
		return mapper.toResponseDTO(clienteUseCase.crear(mapper.toDomain(request)));

	}
	@GetMapping
	public List<ClienteResponseDTO> listar() {
		return clienteUseCase.listar().stream().map(mapper::toResponseDTO).toList();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable int id) {
		clienteUseCase.eliminar(id);
		return ResponseEntity.noContent().build();
	}

}
