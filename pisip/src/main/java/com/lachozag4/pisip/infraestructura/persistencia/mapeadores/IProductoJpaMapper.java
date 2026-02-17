package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.ProductoJpa;

@Mapper(componentModel = "spring", uses = { ICategoriaJpaMapper.class })
public interface IProductoJpaMapper {

	@Mapping(target = "fkCategoria", source = "fkCategoriaId")
	@Mapping(target = "descontarStock", ignore = true)
	@Mapping(target = "restaurarStock", ignore = true)
	Producto toDomain(ProductoJpa entity);

	@Mapping(target = "fkCategoriaId", source = "fkCategoria")
	ProductoJpa toEntity(Producto producto);

}
