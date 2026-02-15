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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IUsuarioUseCase;
import com.lachozag4.pisip.infraestructura.seguridad.JwtUtil;
import com.lachozag4.pisip.presentacion.dto.request.CambiarPasswordRequestDTO;
import com.lachozag4.pisip.presentacion.dto.request.LoginRequestDTO;
import com.lachozag4.pisip.presentacion.dto.request.UsuarioRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.LoginResponseDTO;
import com.lachozag4.pisip.presentacion.dto.response.UsuarioResponseDTO;
import com.lachozag4.pisip.presentacion.mapeadores.IUsuarioDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioControlador {
	private final IUsuarioUseCase usuarioUseCase;
	private final IUsuarioDtoMapper mapper;
	private final JwtUtil jwtUtil;

	public UsuarioControlador(IUsuarioUseCase usuarioUseCase, IUsuarioDtoMapper mapper, JwtUtil jwtUtil) {
		this.usuarioUseCase = usuarioUseCase;
		this.mapper = mapper;
		this.jwtUtil = jwtUtil;
	}

	@GetMapping("/existe-alguno")
	public ResponseEntity<Boolean> existeAlguno() {
		boolean existe = !usuarioUseCase.listar().isEmpty();
		return ResponseEntity.ok(existe);
	}

	@PostMapping("/setup-admin")
	public ResponseEntity<UsuarioResponseDTO> setupAdmin(@Valid @RequestBody UsuarioRequestDTO request) {
		// Solo funciona si no existe ningún usuario en el sistema
		if (!usuarioUseCase.listar().isEmpty()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		request.setRol("ADMIN");
		request.setEstado(true);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(mapper.toResponseDTO(usuarioUseCase.crear(mapper.toDomain(request))));
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
		return usuarioUseCase.autenticar(request.getUsername(), request.getPassword())
				.map(usuario -> {
					String token = jwtUtil.generateToken(usuario.getUsername(), usuario.getRol());
					LoginResponseDTO response = new LoginResponseDTO(
							token,
							usuario.getIdusuario(),
							usuario.getUsername(),
							usuario.getNombreCompleto(),
							usuario.getRol(),
							usuario.isRequiereCambioPassword()
					);
					return ResponseEntity.ok(response);
				})
				.orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
	}

	@PostMapping("/cambiar-password")
	public ResponseEntity<Void> cambiarPassword(@Valid @RequestBody CambiarPasswordRequestDTO request) {
		usuarioUseCase.cambiarPassword(request.getUsername(), request.getPasswordActual(), request.getPasswordNuevo());
		return ResponseEntity.noContent().build();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UsuarioResponseDTO crear(@Valid @RequestBody UsuarioRequestDTO request) {
		return mapper.toResponseDTO(usuarioUseCase.crear(mapper.toDomain(request)));
	}

	@GetMapping
	public ResponseEntity<List<UsuarioResponseDTO>> listar() {
		var lista = usuarioUseCase.listar().stream().map(mapper::toResponseDTO).toList();
		return ResponseEntity.ok(lista);
	}

	@GetMapping("/{id}")
	public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable("id") int id) {
		var usuario = usuarioUseCase.obtenerPorId(id);
		return ResponseEntity.ok(mapper.toResponseDTO(usuario));
	}

	@GetMapping("/por-username/{username}")
	public ResponseEntity<UsuarioResponseDTO> obtenerPorUsername(@PathVariable("username") String username) {
		return usuarioUseCase.buscarPorUsername(username)
				.map(mapper::toResponseDTO)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable("id") int id,
			@Valid @RequestBody UsuarioRequestDTO request) {
		var actualizado = usuarioUseCase.actualizar(id, mapper.toDomain(request));
		return ResponseEntity.ok(mapper.toResponseDTO(actualizado));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") int id) {
		usuarioUseCase.eliminar(id);
		return ResponseEntity.noContent().build();
	}

}
