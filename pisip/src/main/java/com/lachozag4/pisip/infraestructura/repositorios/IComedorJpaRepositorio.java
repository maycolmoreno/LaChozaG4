package com.lachozag4.pisip.infraestructura.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.ComedorJpa;

public interface IComedorJpaRepositorio extends JpaRepository<ComedorJpa, Integer> {

	List<ComedorJpa> findByEstadoTrue();

	List<ComedorJpa> findByEstadoFalse();

	Optional<ComedorJpa> findByNombre(String nombre);
}
