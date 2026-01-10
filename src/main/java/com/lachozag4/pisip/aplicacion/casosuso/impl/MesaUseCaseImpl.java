package com.lachozag4.pisip.aplicacion.casosuso.impl;

import java.util.List;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IMesaUseCase;
import com.lachozag4.pisip.dominio.entidades.Mesa;
import com.lachozag4.pisip.dominio.repositorios.IMesaRepositorio;

public class MesaUseCaseImpl implements IMesaUseCase {

	private final IMesaRepositorio repositorio;

	public MesaUseCaseImpl(IMesaRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public Mesa crear(Mesa mesa) {
		// TODO Auto-generated method stub
		return repositorio.guardar(mesa);
	}

	@Override
	public Mesa obtenerPorId(int id) {
		// TODO Auto-generated method stub
		return repositorio.BuscarPorId(id).orElseThrow(() -> new RuntimeException("Mesa no encontrado"));
	}

	@Override
	public List<Mesa> listar() {
		// TODO Auto-generated method stub
		return repositorio.listarTodos();
	}

	@Override
	public void eliminar(int id) {
		// TODO Auto-generated method stub
		repositorio.eliminar(id);

	}

}
