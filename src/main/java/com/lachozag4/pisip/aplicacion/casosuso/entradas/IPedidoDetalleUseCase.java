package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import java.util.List;

import com.lachozag4.pisip.dominio.entidades.PedidoDetalle;

public interface IPedidoDetalleUseCase {
	PedidoDetalle crear(PedidoDetalle pedidoDetalle);

	PedidoDetalle obtenerPorId(int id);

	List<PedidoDetalle> listar();

	void eliminar(int id);

}
