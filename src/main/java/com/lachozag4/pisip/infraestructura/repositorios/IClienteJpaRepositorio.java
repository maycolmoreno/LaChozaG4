package com.lachozag4.pisip.infraestructura.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.ClienteJpa;

public interface IClienteJpaRepositorio extends JpaRepository<ClienteJpa, Integer> {
	List<ClienteJpa> findByEstadoTrue();
	
	List<ClienteJpa> findByEstadoFalse();
	
	Optional<ClienteJpa> findByCedula(String cedula);
	
	Optional<ClienteJpa> findByEmail(String email);
	
	List<ClienteJpa> findByNombreContaining(String nombre);
}
