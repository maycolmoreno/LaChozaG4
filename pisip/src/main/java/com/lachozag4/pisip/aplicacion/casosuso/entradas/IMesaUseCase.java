package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import java.util.List;

import com.lachozag4.pisip.dominio.entidades.Mesa;

public interface IMesaUseCase {

	Mesa crear(Mesa mesa);

	Mesa obtenerPorId(int idmesa);

	List<Mesa> listar();

	Mesa actualizar(int idmesa, Mesa mesa);

	void eliminar(int idmesa);

	/**
	 * Lista mesas disponibles (sin pedidos activos)
	 */
	List<Mesa> listarDisponibles();

	/**
	 * Lista mesas ocupadas (con pedidos activos)
	 */
	List<Mesa> listarOcupadas();

}
