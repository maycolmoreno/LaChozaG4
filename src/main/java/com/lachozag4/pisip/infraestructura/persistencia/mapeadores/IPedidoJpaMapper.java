package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.PedidoJpa;

@Mapper(componentModel = "spring", uses = { IPedidoDetalleJpaMapper.class, ICuentaJpaMapper.class })
public interface IPedidoJpaMapper {

	@org.mapstruct.Mapping(target = "conEstado", ignore = true)
	Pedido toDomain(PedidoJpa entity);

	@Mapping(target = "detalles", ignore = true)
	PedidoJpa toEntity(Pedido domain);

	@AfterMapping
	default void establecerRelacionBidireccional(Pedido domain, @MappingTarget PedidoJpa pedidoJpa) {
		if (domain.getDetalles() != null && !domain.getDetalles().isEmpty()) {
			domain.getDetalles().forEach(detalleDomain -> {
				var detalleJpa = new com.lachozag4.pisip.infraestructura.persistencia.jpa.PedidoDetalleJpa();
				detalleJpa.setCantidad(detalleDomain.getCantidad());
				detalleJpa.setPrecioUnitario(detalleDomain.getPrecioUnitario());

				var productoJpa = new com.lachozag4.pisip.infraestructura.persistencia.jpa.ProductoJpa();
				productoJpa.setIdproducto(detalleDomain.getProducto().getIdproducto());
				detalleJpa.setFkProducto(productoJpa);

				detalleJpa.setFkPedido(pedidoJpa);
				pedidoJpa.getDetalles().add(detalleJpa);
			});
		}
	}
}
