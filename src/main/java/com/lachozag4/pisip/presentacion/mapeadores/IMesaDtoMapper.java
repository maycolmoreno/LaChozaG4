package com.lachozag4.pisip.presentacion.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.lachozag4.pisip.dominio.entidades.Mesa;
import com.lachozag4.pisip.presentacion.dto.request.MesaRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.MesaResponseDTO;

@Mapper(componentModel = "spring")
public interface IMesaDtoMapper {

	@Mapping(target = "idmesa", ignore = true)
	Mesa toDomain(MesaRequestDTO dto);

	@Mapping(target = "idmesa", source = "idmesa")
	MesaResponseDTO toResponseDTO(Mesa mesa);

}
