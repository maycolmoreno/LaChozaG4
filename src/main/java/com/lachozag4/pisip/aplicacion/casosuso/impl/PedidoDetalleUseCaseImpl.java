package com.lachozag4.pisip.aplicacion.casosuso.impl;

import java.util.List;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IPedidoDetalleUseCase;
import com.lachozag4.pisip.dominio.entidades.PedidoDetalle;
import com.lachozag4.pisip.dominio.repositorios.IPedidoDetalleRepositorio;

public class PedidoDetalleUseCaseImpl implements IPedidoDetalleUseCase {

	private final IPedidoDetalleRepositorio repositorio;

	public PedidoDetalleUseCaseImpl(IPedidoDetalleRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public PedidoDetalle crear(PedidoDetalle pedidoDetalle) {
		// TODO Auto-generated method stub
		return repositorio.guardar(pedidoDetalle);
	}

	@Override
	public PedidoDetalle obtenerPorId(int id) {
		// TODO Auto-generated method stub
		return repositorio.BuscarPorId(id).orElseThrow(() -> new RuntimeException("PedidoDetalle no encontrado"));
	}

	@Override
	public List<PedidoDetalle> listar() {
		// TODO Auto-generated method stub
		return repositorio.listarTodos();
	}

	@Override
	public void eliminar(int id) {
		// TODO Auto-generated method stub
		repositorio.eliminar(id);

	}

}