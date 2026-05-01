package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import java.util.List;

import com.lachozag4.pisip.dominio.entidades.Comedor;

public interface IComedorUseCase {

	Comedor crear(Comedor comedor);

	Comedor buscarPorId(int id);

	List<Comedor> listarTodos();

	List<Comedor> listarActivos();

	Comedor actualizar(int id, Comedor comedor);

	void eliminar(int id);
}
