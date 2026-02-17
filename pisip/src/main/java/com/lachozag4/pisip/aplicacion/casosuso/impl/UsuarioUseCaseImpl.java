package com.lachozag4.pisip.aplicacion.casosuso.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IUsuarioUseCase;
import com.lachozag4.pisip.aplicacion.excepciones.BusinessException;
import com.lachozag4.pisip.aplicacion.excepciones.NotFoundException;
import com.lachozag4.pisip.dominio.entidades.Usuario;
import com.lachozag4.pisip.dominio.repositorios.IUsuarioRepositorio;

public class UsuarioUseCaseImpl implements IUsuarioUseCase {

	private final IUsuarioRepositorio repositorio;
	private final PasswordEncoder passwordEncoder;

	public UsuarioUseCaseImpl(IUsuarioRepositorio repositorio, PasswordEncoder passwordEncoder) {
		this.repositorio = repositorio;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Usuario crear(Usuario usuario) {

		if (usuario.getUsername() == null || usuario.getUsername().isBlank()) {
			throw new IllegalArgumentException("El username es obligatorio");
		}

		// Validar username único
		final String username = usuario.getUsername();
		repositorio.buscarPorUsername(username).ifPresent(existente -> {
			throw new BusinessException("Ya existe un usuario con el username: " + username);
		});

		// Para el PRIMER usuario del sistema no se fuerza cambio de contraseña,
		// ya que será el administrador creado manualmente.
		// Para el resto de usuarios, sí se obliga a cambiarla en el primer login.
		boolean esPrimerUsuario = repositorio.listarTodos().isEmpty();
		boolean requiereCambioPassword = !esPrimerUsuario;
		// Encriptar password antes de guardar
		if (usuario.getPassword() != null && !usuario.getPassword().isBlank()) {
			String encodedPassword = passwordEncoder.encode(usuario.getPassword());
			usuario = new Usuario(usuario.getIdusuario(), usuario.getUsername(), encodedPassword,
					usuario.getNombreCompleto(), usuario.getRol(), usuario.getEstado(),
					requiereCambioPassword);
		}

		return repositorio.guardar(usuario);
	}

	@Override
	public Usuario obtenerPorId(int idusuario) {
		return repositorio.buscarPorId(idusuario)
				.orElseThrow(() -> new NotFoundException("Usuario no encontrado con ID: " + idusuario));
	}

	@Override
	public List<Usuario> listar() {
		// Para permitir que los clientes (por ejemplo, el panel de administración)
		// puedan ver también usuarios inactivos cuando lo necesiten, exponemos
		// todos los usuarios. Los casos que requieran solo activos pueden filtrar
		// por estado a nivel de cliente.
		return repositorio.listarTodos();
	}

	@Override
	public void eliminar(int idusuario) {

		Usuario usuario = obtenerPorId(idusuario);

		Usuario usuarioInactivo = new Usuario(usuario.getIdusuario(), usuario.getUsername(),
				usuario.getPassword(), usuario.getNombreCompleto(), usuario.getRol(), false,
				usuario.isRequiereCambioPassword());

		repositorio.guardar(usuarioInactivo);
	}

	@Override
	public Usuario actualizar(int idusuario, Usuario usuario) {
		Usuario existente = obtenerPorId(idusuario);

		// Validar username único (excluir el propio usuario)
		repositorio.buscarPorUsername(usuario.getUsername()).ifPresent(otro -> {
			if (otro.getIdusuario() != idusuario) {
				throw new BusinessException("Ya existe un usuario con el username: " + usuario.getUsername());
			}
		});

		// Si viene un nuevo password, encriptarlo; si no, mantener el existente
		String password;
		if (usuario.getPassword() != null && !usuario.getPassword().isBlank()) {
			password = passwordEncoder.encode(usuario.getPassword());
		} else {
			password = existente.getPassword();
		}
		var usuarioActualizado = new Usuario(idusuario, usuario.getUsername(),
				password, usuario.getNombreCompleto(), usuario.getRol(), usuario.getEstado(),
				usuario.isRequiereCambioPassword());
		return repositorio.guardar(usuarioActualizado);
	}
	
	@Override
	public Optional<Usuario> autenticar(String username, String password) {
		return repositorio.buscarPorUsername(username)
				.filter(Usuario::getEstado)
				.filter(u -> u.getPassword() != null && passwordEncoder.matches(password, u.getPassword()))
				.map(u -> {
					boolean requiereCambio = u.isRequiereCambioPassword();
					// Regla adicional: si es ADMIN y entra con la clave genérica "admin123",
					// también forzamos cambio aunque el flag aún no esté marcado.
					if (!requiereCambio && "ADMIN".equalsIgnoreCase(u.getRol()) && "admin123".equals(password)) {
						requiereCambio = true;
					}
					if (requiereCambio != u.isRequiereCambioPassword()) {
						return new Usuario(u.getIdusuario(), u.getUsername(), u.getPassword(),
								u.getNombreCompleto(), u.getRol(), u.getEstado(), true);
					}
					return u;
				});
	}
	
	@Override
	public Optional<Usuario> buscarPorUsername(String username) {
		return repositorio.buscarPorUsername(username);
	}
	
	@Override
	public void cambiarPassword(String username, String passwordActual, String passwordNuevo) {
		Usuario usuario = repositorio.buscarPorUsername(username)
				.orElseThrow(() -> new BusinessException("Usuario no encontrado"));
		
		if (!usuario.getEstado()) {
			throw new BusinessException("El usuario se encuentra inactivo");
		}
		
		if (usuario.getPassword() == null || !passwordEncoder.matches(passwordActual, usuario.getPassword())) {
			throw new BusinessException("La contraseña actual no es correcta");
		}
		
		String nuevaEncriptada = passwordEncoder.encode(passwordNuevo);
		Usuario actualizado = new Usuario(usuario.getIdusuario(), usuario.getUsername(), nuevaEncriptada,
				usuario.getNombreCompleto(), usuario.getRol(), usuario.getEstado(), false);
		repositorio.guardar(actualizado);
	}
}
