package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import java.time.LocalDateTime;
import java.util.List;

import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.dominio.entidades.ResultadoPaginado;

public interface IPedidoUseCase {

	Pedido crear(Pedido pedido);

	Pedido obtenerPorId(int idpedido);

	List<Pedido> listar();

	Pedido actualizar(int idpedido, Pedido pedido);

	Pedido cambiarEstado(int idpedido, String estado);

	void eliminar(int idpedido);

	/**
	 * Listado paginado con filtros opcionales (estado, texto libre, rango de fechas).
	 */
	ResultadoPaginado<Pedido> listarPaginado(String estado, String q,
			LocalDateTime fechaDesde, LocalDateTime fechaHasta,
			int page, int size);
}