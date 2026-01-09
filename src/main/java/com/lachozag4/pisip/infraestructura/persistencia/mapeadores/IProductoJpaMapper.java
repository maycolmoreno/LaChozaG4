package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.ProductoJpa;

@Mapper(componentModel = "spring")
public interface IProductoJpaMapper {
	Producto toDomain(ProductoJpa entity);
	ProductoJpa toEntity(Producto producto);
}
