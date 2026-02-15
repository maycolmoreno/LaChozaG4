package com.lachozag4.pisip.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.Mesa;

public interface IMesaRepositorio {
	Mesa guardar(Mesa mesa);
	Optional<Mesa> buscarPorId(int idmesa);
	Optional<Mesa> buscarPorNumero(int numero);
	List<Mesa> listarDisponibles();
	List<Mesa> listarTodas();
	Mesa actualizar(Mesa mesa);
	boolean existePorId(int idmesa);
	void eliminar(int idmesa);
	
	/**
	 * Lista mesas que NO tienen pedidos activos (disponibles para nuevos clientes)
	 */
	List<Mesa> listarMesasSinPedidosActivos();
	
	/**
	 * Lista mesas que tienen pedidos activos (ocupadas)
	 */
	List<Mesa> listarMesasOcupadas();
}
