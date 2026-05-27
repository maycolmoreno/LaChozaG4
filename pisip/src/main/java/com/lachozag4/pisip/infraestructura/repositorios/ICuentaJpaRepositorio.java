package com.lachozag4.pisip.infraestructura.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lachozag4.pisip.dominio.enums.EstadoCuenta;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.CuentaJpa;

public interface ICuentaJpaRepositorio extends JpaRepository<CuentaJpa, Integer> {

	List<CuentaJpa> findByEstado(EstadoCuenta estado);

	Optional<CuentaJpa> findFirstByEstadoAndFkMesa_Idmesa(EstadoCuenta estado, int idMesa);
}
