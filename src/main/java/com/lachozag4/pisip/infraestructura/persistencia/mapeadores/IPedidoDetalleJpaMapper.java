package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.lachozag4.pisip.dominio.entidades.PedidoDetalle;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.PedidoDetalleJpa;

@Mapper(componentModel = "spring", uses = { IProductoJpaMapper.class })
public interface IPedidoDetalleJpaMapper {

	@Mapping(target = "fkPedido", ignore = true)
	PedidoDetalle toDomain(PedidoDetalleJpa entity);

	@Mapping(target = "fkPedido", ignore = true)
	PedidoDetalleJpa toEntity(PedidoDetalle domain);

	List<PedidoDetalle> toDomainList(List<PedidoDetalleJpa> detalles);

	List<PedidoDetalleJpa> toEntityList(List<PedidoDetalle> detalles);
}
