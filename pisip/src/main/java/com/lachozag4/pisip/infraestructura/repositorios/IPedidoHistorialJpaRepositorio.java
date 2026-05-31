package com.lachozag4.pisip.infraestructura.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.PedidoHistorialJpa;

public interface IPedidoHistorialJpaRepositorio extends JpaRepository<PedidoHistorialJpa, Integer> {

    List<PedidoHistorialJpa> findByFkPedido_IdpedidoOrderByFechaAscIdhistorialAsc(int idpedido);
}
