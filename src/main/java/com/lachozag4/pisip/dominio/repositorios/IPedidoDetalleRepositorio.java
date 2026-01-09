package com.lachozag4.pisip.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.PedidoDetalle;

public interface IPedidoDetalleRepositorio {
	PedidoDetalle guardar(PedidoDetalle pedidodetalle);
	Optional<PedidoDetalle> BuscarPorId(int id);
	List<PedidoDetalle> listarTodos();
	void eliminar (int id);

}
