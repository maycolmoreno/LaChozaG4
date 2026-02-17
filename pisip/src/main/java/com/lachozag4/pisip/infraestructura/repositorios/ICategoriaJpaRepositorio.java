package com.lachozag4.pisip.infraestructura.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.CategoriaJpa;

public interface ICategoriaJpaRepositorio extends JpaRepository<CategoriaJpa, Integer> {
	
	List<CategoriaJpa> findByEstadoTrue();
	
	List<CategoriaJpa> findByEstadoFalse();
	
	Optional<CategoriaJpa> findByNombre(String nombre);
	
	List<CategoriaJpa> findByNombreContaining(String nombre);
}
