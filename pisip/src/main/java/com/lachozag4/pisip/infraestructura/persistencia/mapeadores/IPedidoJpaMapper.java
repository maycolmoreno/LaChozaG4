package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.dominio.enums.EstadoPedido;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.PedidoJpa;

@Mapper(componentModel = "spring", imports = { EstadoPedido.class }, uses = { IPedidoDetalleJpaMapper.class, ICuentaJpaMapper.class, IMesaJpaMapper.class, IClienteJpaMapper.class, IUsuarioJpaMapper.class })
public interface IPedidoJpaMapper {

	@org.mapstruct.Mapping(target = "conEstado", ignore = true)
	@Mapping(target = "estado", expression = "java(entity.getEstado() != null ? entity.getEstado().name() : null)")
	Pedido toDomain(PedidoJpa entity);

	@Mapping(target = "detalles", ignore = true)
	@Mapping(target = "estado", expression = "java(domain.getEstado() != null ? EstadoPedido.valueOf(domain.getEstado()) : null)")
	PedidoJpa toEntity(Pedido domain);

	default String estadoToString(EstadoPedido e) { return e == null ? null : e.name(); }
	default EstadoPedido stringToEstado(String s) { return s == null ? null : EstadoPedido.valueOf(s); }

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
