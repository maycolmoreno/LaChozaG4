package com.lachozag4.pisip.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.Categoria;

public interface ICategoriaRepositorio {
	Categoria guardar(Categoria categoria);
	Optional<Categoria> BuscarPorId(int id);
	List<Categoria> listarTodos();
	void eliminar (int id);

}
