package com.lachozag4.pisip.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.presentacion.dto.request.ProductoRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.ProductoResponseDTO;

@Mapper(componentModel = "spring")
public interface IProductoDtoMapper {

	Producto toDomain(ProductoRequestDTO dto);

	ProductoResponseDTO toResponseDTO(Producto producto);
}
