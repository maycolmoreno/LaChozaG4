package com.lachozag4.pisip.presentacion.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.presentacion.dto.response.ProductoResponseDTO;

@Mapper(componentModel = "spring")
public interface IProductoDtoMapper {

	@Mapping(target = "categoriaId", source = "fkCategoria.idcategoria")
	ProductoResponseDTO toResponseDTO(Producto producto);

}
