package com.lachozag4.pisip.infraestructura.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lachozag4.pisip.dominio.enums.EstadoCuenta;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.CuentaJpa;

import jakarta.persistence.LockModeType;

public interface ICuentaJpaRepositorio extends JpaRepository<CuentaJpa, Integer> {

	List<CuentaJpa> findByEstado(EstadoCuenta estado);

	Optional<CuentaJpa> findFirstByEstadoAndFkMesa_Idmesa(EstadoCuenta estado, int idMesa);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT c FROM CuentaJpa c WHERE c.idcuenta = :id")
	Optional<CuentaJpa> findByIdForUpdate(@Param("id") int id);
}
