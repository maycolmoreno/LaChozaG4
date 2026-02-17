package com.lachozag4.pisip.presentacion.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.lachozag4.pisip.dominio.entidades.Cliente;
import com.lachozag4.pisip.presentacion.dto.request.ClienteRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.ClienteResponseDTO;

@Mapper(componentModel = "spring")
public interface IClienteDtoMapper {

	@Mapping(target = "idcliente", ignore = true)
	Cliente toDomain(ClienteRequestDTO dto);

	@Mapping(target = "idcliente", source = "idcliente")
	ClienteResponseDTO toResponseDTO(Cliente cliente);

}
