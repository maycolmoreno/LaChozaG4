package com.lachozag4.pisip.infraestructura.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.CuentaJpa;

public interface ICuentaJpaRepositorio extends JpaRepository<CuentaJpa, Integer> {

	List<CuentaJpa> findByEstado(String estado);
}
