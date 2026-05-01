package com.lachozag4.pisip.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.Comedor;

public interface IComedorRepositorio {

	Comedor guardar(Comedor comedor);

	Optional<Comedor> buscarPorId(int idcomedor);

	Optional<Comedor> buscarPorNombre(String nombre);

	List<Comedor> listarActivos();

	List<Comedor> listarTodos();

	Comedor actualizar(Comedor comedor);

	void eliminar(int idcomedor);
}
