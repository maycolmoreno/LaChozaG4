package com.lachozag4.pisip.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.lachozag4.pisip.dominio.entidades.Cliente;
import com.lachozag4.pisip.presentacion.dto.request.ClienteRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.ClienteResponseDTO;

@Mapper(componentModel = "spring")
public interface IClienteDtoMapper {
	Cliente toDomain(ClienteRequestDTO dto);

	ClienteResponseDTO toResponseDTO(Cliente cliente);

}
