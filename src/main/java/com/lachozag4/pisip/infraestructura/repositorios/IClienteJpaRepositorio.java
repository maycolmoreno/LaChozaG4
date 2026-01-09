package com.lachozag4.pisip.infraestructura.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.ClienteJpa;

public interface IClienteJpaRepositorio extends JpaRepository<ClienteJpa, Integer> {

}
