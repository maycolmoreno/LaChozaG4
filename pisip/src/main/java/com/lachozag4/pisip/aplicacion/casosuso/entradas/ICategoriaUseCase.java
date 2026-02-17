package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import java.util.List;

import com.lachozag4.pisip.dominio.entidades.Categoria;

public interface ICategoriaUseCase {

	Categoria crear(Categoria categoria);

    Categoria buscarPorId(int id);

    List<Categoria> listarTodos();

    List<Categoria> listarActivas();

	Categoria actualizar(int id, Categoria categoria);


    void eliminar(int id);
}
