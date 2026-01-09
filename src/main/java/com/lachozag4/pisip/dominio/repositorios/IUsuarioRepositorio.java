package com.lachozag4.pisip.dominio.repositorios;

import java.util.List;
import java.util.Optional;


import com.lachozag4.pisip.dominio.entidades.Usuario;

public interface IUsuarioRepositorio {
	Usuario guardar(Usuario usuario);
	Optional<Usuario> BuscarPorId(int id);
	List<Usuario> listarTodos();
	void eliminar (int id);

}
