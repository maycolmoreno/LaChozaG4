package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import java.util.List;
import com.lachozag4.pisip.dominio.entidades.PedidoDetalle;

public interface IPedidoDetalleUseCase {

    List<PedidoDetalle> listarPorPedido(int idPedido);

    PedidoDetalle obtenerPorId(int idPedido, int idDetalle);

    PedidoDetalle crear(int idPedido, int idProducto, int cantidad, double precioUnitario);

    PedidoDetalle actualizar(int idPedido, int idDetalle, int idProducto, int cantidad, double precioUnitario);

    void eliminar(int idPedido, int idDetalle);

}