package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import com.lachozag4.pisip.dominio.entidades.PedidoDetalle;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.PedidoDetalleEntity;

public class PedidoDetalleMapper {

	public static PedidoDetalle aDominio(PedidoDetalleEntity entity) {
		if (entity == null)
			return null;

		PedidoDetalle dominio = new PedidoDetalle();
		dominio.setId(entity.getId());
		// Usamos el ProductoMapper para convertir el ceviche relacionado
		dominio.setProducto(ProductoMapper.aDominio(entity.getProducto()));
		dominio.setCantidad(entity.getCantidad());
		dominio.setPrecioUnitario(entity.getPrecioUnitario());

		return dominio;
	}

	public static PedidoDetalleEntity aEntity(PedidoDetalle dominio) {
		if (dominio == null)
			return null;

		PedidoDetalleEntity entity = new PedidoDetalleEntity();
		entity.setId(dominio.getId());
		// Convertimos el objeto Producto de dominio a la entidad JPA
		entity.setProducto(ProductoMapper.aEntity(dominio.getProducto()));
		entity.setCantidad(dominio.getCantidad());
		entity.setPrecioUnitario(dominio.getPrecioUnitario());
		// El subtotal se calcula automáticamente en la entidad por el @PrePersist

		return entity;
	}
}