package com.lachozag4.pisip.infraestructura.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.PedidoDetalleJpa;

public interface IPedidoDetalleJpaRepositorio extends JpaRepository<PedidoDetalleJpa, Integer> {
	
	// fkPedido y fkProducto son los nombres reales de los campos en PedidoDetalleJpa
	List<PedidoDetalleJpa> findByFkPedido_Idpedido(int idPedido);
	
	List<PedidoDetalleJpa> findByFkProducto_Idproducto(int idProducto);
	
	Optional<PedidoDetalleJpa> findByFkPedido_IdpedidoAndFkProducto_Idproducto(int idPedido, int idProducto);
}
