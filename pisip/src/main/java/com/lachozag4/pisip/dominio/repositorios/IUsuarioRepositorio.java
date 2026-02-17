package com.lachozag4.pisip.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.Usuario;

public interface IUsuarioRepositorio {
	Usuario guardar(Usuario usuario);
	Optional<Usuario> buscarPorId(int idusuario);
	Optional<Usuario> buscarPorUsername(String username);
	List<Usuario> listarDisponibles();
	List<Usuario> listarTodos();
	Usuario actualizar(Usuario usuario);
	boolean existePorId(int idusuario);
	void eliminar(int idusuario);
}
