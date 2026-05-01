package com.lachozag4.pisip.aplicacion.casosuso.impl;

import java.util.List;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IComedorUseCase;
import com.lachozag4.pisip.aplicacion.excepciones.BusinessException;
import com.lachozag4.pisip.aplicacion.excepciones.NotFoundException;
import com.lachozag4.pisip.dominio.entidades.Comedor;
import com.lachozag4.pisip.dominio.repositorios.IComedorRepositorio;

public class ComedorUseCaseImpl implements IComedorUseCase {

	private final IComedorRepositorio repositorio;

	public ComedorUseCaseImpl(IComedorRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public Comedor crear(Comedor comedor) {
		repositorio.buscarPorNombre(comedor.getNombre()).ifPresent(existente -> {
			throw new BusinessException("Ya existe un comedor con el nombre: " + comedor.getNombre());
		});
		return repositorio.guardar(comedor);
	}

	@Override
	public Comedor buscarPorId(int id) {
		return repositorio.buscarPorId(id)
				.orElseThrow(() -> new NotFoundException("Comedor no encontrado con id: " + id));
	}

	@Override
	public List<Comedor> listarTodos() {
		return repositorio.listarTodos();
	}

	@Override
	public List<Comedor> listarActivos() {
		return repositorio.listarActivos();
	}

	@Override
	public Comedor actualizar(int id, Comedor comedor) {
		buscarPorId(id);
		repositorio.buscarPorNombre(comedor.getNombre()).ifPresent(existente -> {
			if (existente.getIdcomedor() != id) {
				throw new BusinessException("Ya existe un comedor con el nombre: " + comedor.getNombre());
			}
		});
		Comedor actualizado = new Comedor(id, comedor.getNombre(), comedor.getDescripcion(), comedor.getEstado());
		return repositorio.actualizar(actualizado);
	}

	@Override
	public void eliminar(int id) {
		Comedor comedor = buscarPorId(id);
		Comedor desactivado = new Comedor(comedor.getIdcomedor(), comedor.getNombre(),
				comedor.getDescripcion(), false);
		repositorio.guardar(desactivado);
	}
}
