package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import java.util.List;

import com.lachozag4.pisip.dominio.entidades.Pedido;

public interface IPedidoUseCase {

	Pedido crear(Pedido pedido);

	Pedido obtenerPorId(int id);

	List<Pedido> listar();

	void eliminar(int id);

}
