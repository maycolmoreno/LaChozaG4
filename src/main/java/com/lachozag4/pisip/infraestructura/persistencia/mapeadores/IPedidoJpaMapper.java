package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.PedidoJpa;

@Mapper(componentModel = "spring")
public interface IPedidoJpaMapper {
	Pedido toDomain(PedidoJpa entity);
	PedidoJpa toEntity(Pedido pedido);

}
