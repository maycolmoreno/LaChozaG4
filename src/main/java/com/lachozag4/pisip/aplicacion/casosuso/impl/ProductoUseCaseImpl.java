package com.lachozag4.pisip.aplicacion.casosuso.impl;

import java.util.List;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IProductoUseCase;
import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.dominio.repositorios.IProductoRepositorio;

public class ProductoUseCaseImpl implements IProductoUseCase {

	private final IProductoRepositorio repositorio;

	public ProductoUseCaseImpl(IProductoRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public Producto crear(Producto producto) {
		// TODO Auto-generated method stub
		return repositorio.guardar(producto);
	}

	@Override
	public Producto obtenerPorId(int id) {
		// TODO Auto-generated method stub
		return repositorio.BuscarPorId(id).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
	}

	@Override
	public List<Producto> listar() {
		// TODO Auto-generated method stub
		return repositorio.listarTodos();
	}

	@Override
	public void eliminar(int id) {
		// TODO Auto-generated method stub
		repositorio.eliminar(id);

	}

}
