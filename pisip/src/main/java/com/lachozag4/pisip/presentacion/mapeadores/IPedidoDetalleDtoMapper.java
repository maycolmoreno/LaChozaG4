package com.lachozag4.pisip.presentacion.mapeadores;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.lachozag4.pisip.dominio.entidades.PedidoDetalle;
import com.lachozag4.pisip.presentacion.dto.response.PedidoDetalleResponseDTO;

@Mapper(
    componentModel = "spring",
    uses = { IProductoDtoMapper.class },
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface IPedidoDetalleDtoMapper {

    // Dominio -> DTO
    @Mapping(target = "idpedidodetalle", source = "idpedidodetalle")
    @Mapping(target = "fkProducto",      source = "fkProducto")
    @Mapping(target = "cantidad",        source = "cantidad")
    @Mapping(target = "precioUnitario",  source = "precioUnitario")
    @Mapping(target = "subtotal", expression = "java(d.getCantidad() * d.getPrecioUnitario())")
    // 'subtotal' lo seteamos en @AfterMapping (no necesitas añadir aquí)
    PedidoDetalleResponseDTO toResponseDTO(PedidoDetalle d);

    List<PedidoDetalleResponseDTO> toResponseDTOList(List<PedidoDetalle> detalles);

    @AfterMapping
    default void calcularSubtotal(PedidoDetalle source, @MappingTarget PedidoDetalleResponseDTO target) {
        target.setSubtotal(source.getCantidad() * source.getPrecioUnitario());
    }
}