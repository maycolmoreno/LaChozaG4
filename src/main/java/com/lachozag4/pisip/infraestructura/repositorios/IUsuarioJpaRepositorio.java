package com.lachozag4.pisip.infraestructura.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.UsuarioJpa;

public interface IUsuarioJpaRepositorio extends JpaRepository<UsuarioJpa, Integer>{

}
