package com.lachozag4.pisip.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.Producto;

public interface IProductoRepositorio {

	Producto guardar(Producto producto);

	Optional<Producto> buscarPorId(int id);

	Optional<Producto> buscarPorNombreYCategoria(String nombre, int idCategoria);

	List<Producto> listarTodos();

	List<Producto> listarActivos();

	List<Producto> listarPorCategoria(int idCategoria);

	Producto actualizar(Producto producto);

	boolean existePorId(int id);

	void eliminar(int id);
}
