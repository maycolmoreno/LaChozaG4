package com.lachozag4.pisip.infraestructura.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.CajaTurnoJpa;

public interface ICajaTurnoJpaRepositorio extends JpaRepository<CajaTurnoJpa, Integer> {

	Optional<CajaTurnoJpa> findFirstByEstadoOrderByIdcajaDesc(String estado);
}
