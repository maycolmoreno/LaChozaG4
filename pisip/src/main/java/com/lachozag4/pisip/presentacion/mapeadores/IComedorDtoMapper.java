package com.lachozag4.pisip.presentacion.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.lachozag4.pisip.dominio.entidades.Comedor;
import com.lachozag4.pisip.presentacion.dto.request.ComedorRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.ComedorResponseDTO;

@Mapper(componentModel = "spring")
public interface IComedorDtoMapper {

	@Mapping(target = "idcomedor", ignore = true)
	Comedor toDomain(ComedorRequestDTO dto);

	ComedorResponseDTO toResponseDTO(Comedor comedor);
}
