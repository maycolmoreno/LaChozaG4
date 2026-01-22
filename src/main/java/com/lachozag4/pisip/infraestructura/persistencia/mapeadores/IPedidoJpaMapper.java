package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.PedidoJpa;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.ProductoJpa;

@Mapper(componentModel = "spring", 
        uses = {IUsuarioJpaMapper.class, IMesaJpaMapper.class, IClienteJpaMapper.class})
public interface IPedidoJpaMapper {
	Pedido toDomain(PedidoJpa entity);
	PedidoJpa toEntity(Pedido pedido);
}