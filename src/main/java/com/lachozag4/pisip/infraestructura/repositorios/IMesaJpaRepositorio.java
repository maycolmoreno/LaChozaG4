package com.lachozag4.pisip.infraestructura.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.MesaJpa;

public interface IMesaJpaRepositorio extends JpaRepository<MesaJpa, Integer> {
	List<MesaJpa> findByEstadoTrue();
	
	List<MesaJpa> findByEstadoFalse();
	
	Optional<MesaJpa> findByNumero(int numero);
	
	List<MesaJpa> findByCapacidad(int capacidad);
	
	List<MesaJpa> findByCapacidadGreaterThanEqual(int capacidad);

	/**
	 * Lista mesas activas que NO tienen pedidos pendientes (estado = 'PENDIENTE', 'EN_COCINA' o 'LISTO_PARA_ENTREGA')
	 */
	@Query("SELECT m FROM MesaJpa m WHERE m.estado = true AND m.idmesa NOT IN " +
		"(SELECT p.fkMesa.idmesa FROM PedidoJpa p WHERE p.estado IN ('PENDIENTE', 'EN_COCINA', 'LISTO_PARA_ENTREGA'))")
	List<MesaJpa> findMesasSinPedidosActivos();

	/**
	 * Lista mesas que tienen pedidos pendientes, en cocina o listos para entrega
	 */
	@Query("SELECT DISTINCT m FROM MesaJpa m JOIN PedidoJpa p ON p.fkMesa.idmesa = m.idmesa WHERE p.estado IN ('PENDIENTE', 'EN_COCINA', 'LISTO_PARA_ENTREGA')")
	List<MesaJpa> findMesasConPedidosActivos();
}
