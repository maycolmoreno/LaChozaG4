package com.lachozag4.pisip.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.Producto;

public interface IProductoRepositorio {
	
	Producto guardar(Producto producto);
	Optional<Producto> BuscarPorId(int id);
	List<Producto> listarTodos();
	void eliminar (int id);

}
