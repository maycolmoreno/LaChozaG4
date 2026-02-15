package com.lachozag4.pisip.presentacion.mapeadores;

import java.util.List;

import org.springframework.stereotype.Component;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IClienteUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IMesaUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IProductoUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.entradas.IUsuarioUseCase;
import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.dominio.entidades.PedidoDetalle;
import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.presentacion.dto.request.PedidoDetalleRequestDTO;
import com.lachozag4.pisip.presentacion.dto.request.PedidoRequestDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PedidoRequestMapper {

    private final IUsuarioUseCase usuarioUseCase;
    private final IMesaUseCase mesaUseCase;
    private final IClienteUseCase clienteUseCase;
    private final IProductoUseCase productoUseCase;

    /**
     * Convierte un PedidoRequestDTO en un agregado de dominio Pedido, resolviendo
     * las referencias por ID mediante los casos de uso existentes.
     */
    public Pedido toDomain(PedidoRequestDTO dto) {
        var usuario = usuarioUseCase.obtenerPorId(dto.getIdUsuario());
        var mesa    = mesaUseCase.obtenerPorId(dto.getIdMesa());
        var cliente = clienteUseCase.obtenerPorId(dto.getIdCliente());

        List<PedidoDetalle> detalles = dto.getDetalles().stream()
                .map(this::mapDetalle)
                .toList();

        // Para creación asumimos idpedido = 0 (lo genera la BD)
        Pedido pedido = new Pedido(
                0,
                dto.getFecha(),
                dto.getEstado(),
                dto.getObservaciones(),
                usuario,
                mesa,
                cliente,
                null,
                detalles
        );

        // Establecer la relación inversa fkPedido en cada detalle
        detalles.forEach(d -> d.setFkPedido(pedido));

        return pedido;
    }

    private PedidoDetalle mapDetalle(PedidoDetalleRequestDTO dto) {
        Producto producto = productoUseCase.buscarPorId(dto.getIdProducto());

        return new PedidoDetalle(
                dto.getIdpedidodetalle(),
                producto,
                null, // se completa luego en el agregado Pedido
                dto.getCantidad(),
                dto.getPrecioUnitario()
        );
    }
}
