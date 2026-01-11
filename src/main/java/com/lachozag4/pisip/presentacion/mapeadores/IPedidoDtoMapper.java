package com.lachozag4.pisip.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.presentacion.dto.request.PedidoRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.PedidoResponseDTO;

@Mapper(componentModel = "spring")

public interface IPedidoDtoMapper {
	Pedido toDomain(PedidoRequestDTO dto);

	PedidoResponseDTO toResponseDTO(Pedido pedido);

}
