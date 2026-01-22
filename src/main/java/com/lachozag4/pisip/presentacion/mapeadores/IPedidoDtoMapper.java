package com.lachozag4.pisip.presentacion.mapeadores;
import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.presentacion.dto.request.PedidoRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.PedidoResponseDTO;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IPedidoDtoMapper {

	Pedido toDomain(PedidoRequestDTO dto);

	PedidoResponseDTO toResponseDTO(Pedido pedido);
}