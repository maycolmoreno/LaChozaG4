package com.lachozag4.pisip.dominio.repositorios;

import java.time.LocalDate;
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
	 * Lista los pedidos activos (pendientes) en una mesa específica.
	 * Retorna List para manejar correctamente el caso de múltiples pedidos activos.
	 */
	List<Pedido> buscarPedidosActivosPorMesa(int idMesa);

	/**
	 * Verifica si existen pedidos activos en una mesa, excluyendo el pedido indicado.
	 * Más eficiente que cargar todos los pedidos y filtrar en memoria.
	 */
	boolean existePedidoActivoPorMesa(int idMesa, int excluirIdPedido);

	/**
	 * Lista todos los pedidos asociados a una cuenta específica.
	 */
	List<Pedido> listarPorCuenta(int idcuenta);

	/**
	 * Lista los pedidos COMPLETADOS en una fecha específica.
	 * Filtra en BD (solo trae los del día), sin cargar la tabla completa en memoria.
	 */
	List<Pedido> listarCompletadosPorFecha(LocalDate fecha);

	/**
	 * Búsqueda paginada con filtros opcionales en servidor.
	 */
	ResultadoPaginado<Pedido> listarPaginado(String estado, String q,
			LocalDateTime fechaDesde, LocalDateTime fechaHasta,
			int page, int size);
}