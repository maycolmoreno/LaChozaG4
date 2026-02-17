package com.lachozag4.pisip.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.PedidoDetalle;

public interface IPedidoDetalleRepositorio {

	PedidoDetalle guardar(PedidoDetalle detalle);

	Optional<PedidoDetalle> buscarPorId(int id);

	List<PedidoDetalle> listarTodos();

	List<PedidoDetalle> listarPorPedido(int idPedido);

	PedidoDetalle actualizar(PedidoDetalle detalle);

	boolean existePorId(int id);

	void eliminar(int id);
}