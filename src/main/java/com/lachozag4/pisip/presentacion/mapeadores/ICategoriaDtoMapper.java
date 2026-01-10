package com.lachozag4.pisip.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.lachozag4.pisip.dominio.entidades.Categoria;
import com.lachozag4.pisip.presentacion.dto.request.CategoriaRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.CategoriaResponseDTO;

@Mapper(componentModel = "spring")

public interface ICategoriaDtoMapper {

	Categoria toDomain(CategoriaRequestDTO dto);

	CategoriaResponseDTO toResponseDTO(Categoria categoria);
}
