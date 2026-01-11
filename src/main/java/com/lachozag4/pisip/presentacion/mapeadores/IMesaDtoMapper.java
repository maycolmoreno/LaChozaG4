package com.lachozag4.pisip.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.lachozag4.pisip.dominio.entidades.Mesa;
import com.lachozag4.pisip.presentacion.dto.request.MesaRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.MesaResponseDTO;

@Mapper(componentModel = "spring")

public interface IMesaDtoMapper {
	Mesa toDomain(MesaRequestDTO dto);

	MesaResponseDTO toResponseDTO(Mesa mesa);

}
