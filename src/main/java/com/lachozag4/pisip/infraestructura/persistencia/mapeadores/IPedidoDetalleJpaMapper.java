package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.lachozag4.pisip.dominio.entidades.PedidoDetalle;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.PedidoDetalleJpa;

@Mapper(componentModel = "spring")
public interface IPedidoDetalleJpaMapper {
	PedidoDetalle toDomain(PedidoDetalleJpa entity);
	PedidoDetalleJpa toEntity(PedidoDetalle pedidoDetalle);

}
