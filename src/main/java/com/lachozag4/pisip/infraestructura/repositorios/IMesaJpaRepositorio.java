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
	 * Lista mesas activas que NO tienen cuentas abiertas.
	 */
	@Query("SELECT m FROM MesaJpa m WHERE m.estado = true AND m.idmesa NOT IN " +
		"(SELECT c.fkMesa.idmesa FROM CuentaJpa c WHERE c.estado = 'ABIERTA')")
	List<MesaJpa> findMesasSinPedidosActivos();

	/**
	 * Lista mesas que tienen cuentas abiertas (ocupadas).
	 */
	@Query("SELECT DISTINCT m FROM MesaJpa m JOIN CuentaJpa c ON c.fkMesa.idmesa = m.idmesa WHERE c.estado = 'ABIERTA'")
	List<MesaJpa> findMesasConPedidosActivos();
}
