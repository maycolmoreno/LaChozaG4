package com.lachozag4.pisip.aplicacion.casosuso.impl;

import java.util.List;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.ICategoriaUseCase;
import com.lachozag4.pisip.dominio.entidades.Categoria;
import com.lachozag4.pisip.dominio.repositorios.ICategoriaRepositorio;

public class CategoriaUseCaseImpl implements ICategoriaUseCase {
	private final ICategoriaRepositorio repositorio;

	public CategoriaUseCaseImpl(ICategoriaRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public Categoria crear(Categoria categoria) {
		// TODO Auto-generated method stub
		return repositorio.guardar(categoria);
	}

	@Override
	public Categoria obtenerPorId(int id) {
		// TODO Auto-generated method stub
		return repositorio.BuscarPorId(id).orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
	}

	@Override
	public List<Categoria> listar() {
		// TODO Auto-generated method stub
		return repositorio.listarTodos();
	}

	@Override
	public void eliminar(int id) {
		// TODO Auto-generated method stub
		repositorio.eliminar(id);

	}

}
