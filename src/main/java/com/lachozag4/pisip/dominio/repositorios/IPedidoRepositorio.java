package com.lachozag4.pisip.dominio.repositorios;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.dominio.entidades.ResultadoPaginado;

public interface IPedidoRepositorio {

	Pedido guardar(Pedido pedido);

	Optional<Pedido> buscarPorId(int id);

	List<Pedido> listarTodos();

	List<Pedido> listarPendientes();

	List<Pedido> listarCompletados();

	Pedido actualizar(Pedido pedido);

	boolean existePorId(int id);

	void eliminar(int id);

	/**
	 * Busca si existe un pedido activo (pendiente) en una mesa específica
	 */
	Optional<Pedido> buscarPedidoActivoPorMesa(int idMesa);

	/**
	 * Lista todos los pedidos asociados a una cuenta específica.
	 */
	List<Pedido> listarPorCuenta(int idcuenta);

	/**
	 * Búsqueda paginada con filtros opcionales en servidor.
	 */
	ResultadoPaginado<Pedido> listarPaginado(String estado, String q,
			LocalDateTime fechaDesde, LocalDateTime fechaHasta,
			int page, int size);
}