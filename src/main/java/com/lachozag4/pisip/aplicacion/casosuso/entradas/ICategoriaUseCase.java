package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import java.util.List;

import com.lachozag4.pisip.dominio.entidades.Categoria;

public interface ICategoriaUseCase {

	Categoria crear(Categoria categoria);

	Categoria obtenerPorId(int id);

	List<Categoria> listar();

	void eliminar(int id);

}
