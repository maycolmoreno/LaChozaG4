package com.lachozag4.pisip.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.lachozag4.pisip.dominio.entidades.PedidoDetalle;
import com.lachozag4.pisip.presentacion.dto.request.PedidoDetalleRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.PedidoDetalleResponseDTO;

@Mapper(componentModel = "spring")

public interface IPedidoDetalleDtoMapper {
	PedidoDetalle toDomain(PedidoDetalleRequestDTO dto);

	PedidoDetalleResponseDTO toResponseDTO(PedidoDetalle pedidoDetalle);

}
