package com.lachozag4.pisip.aplicacion.casosuso.impl;

import java.util.List;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IPedidoUseCase;
import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.dominio.repositorios.IPedidoRepositorio;

public class PedidoUseCaseImpl implements IPedidoUseCase {

	private final IPedidoRepositorio repositorio;

	public PedidoUseCaseImpl(IPedidoRepositorio repositorio) {

		this.repositorio = repositorio;
	}

	@Override
	public Pedido crear(Pedido pedido) {

		return repositorio.guardar(pedido);
	}

	@Override
	public Pedido obtenerPorId(int id) {
		return repositorio.BuscarPorId(id).orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
	}

	@Override
	public List<Pedido> listar() {
		return repositorio.listarTodos();
	}

	@Override
	public void eliminar(int id) {
		repositorio.eliminar(id);
	}
}