package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import java.util.List;

import com.lachozag4.pisip.dominio.entidades.Producto;

public interface IProductoUseCase {
	
	Producto crear(Producto producto);

	Producto obtenerPorId(int id);

	List<Producto> listar();

	void eliminar(int id);

}
