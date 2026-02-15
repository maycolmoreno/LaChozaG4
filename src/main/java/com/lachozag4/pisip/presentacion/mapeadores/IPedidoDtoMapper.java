package com.lachozag4.pisip.presentacion.mapeadores;

import java.util.Optional;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.presentacion.dto.response.PedidoResponseDTO;


@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = {
        IUsuarioDtoMapper.class, // si anidas usuario en el response
        IMesaDtoMapper.class,
        IClienteDtoMapper.class,
        IPedidoDetalleDtoMapper.class // si decides incluir detalles en el response del pedido
    }
)
public interface IPedidoDtoMapper {

    // Dominio -> Response
    @Mapping(target = "idpedido",     source = "idpedido")
    @Mapping(target = "fecha",        source = "fecha")
    @Mapping(target = "estado",       source = "estado")
    @Mapping(target = "observaciones",source = "observaciones")
    @Mapping(target = "fkUsuario",      source = "fkUsuario") // se resuelve con IUsuarioDtoMapper
    @Mapping(target = "fkMesa",         source = "fkMesa")
    @Mapping(target = "fkCliente",      source = "fkCliente")
    @Mapping(target = "detalle",        source = "detalles")

    // Calcularemos después de mapear
    @Mapping(target = "cantidadProductos", ignore = true)
    @Mapping(target = "total",              ignore = true)

    PedidoResponseDTO toResponseDTO(Pedido pedido);

    @AfterMapping
    default void calcularTotales(Pedido source, @MappingTarget PedidoResponseDTO target) {
        if (source.getDetalles() == null || source.getDetalles().isEmpty()) {
            return;
        }

        int cantidadProductos = source.getDetalles().stream()
                .mapToInt(d -> Optional.ofNullable(d).map(PedidoDetalle -> PedidoDetalle.getCantidad()).orElse(0))
                .sum();

        double total = source.getDetalles().stream()
                .mapToDouble(d -> Optional.ofNullable(d)
                        .map(PedidoDetalle -> PedidoDetalle.getCantidad() * PedidoDetalle.getPrecioUnitario())
                        .orElse(0.0))
                .sum();

        target.setCantidadProductos(cantidadProductos);
        target.setTotal(total);
    }
}
