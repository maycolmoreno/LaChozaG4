package com.lachozag4.pisip.aplicacion.servicios;

import java.util.List;

import com.lachozag4.pisip.aplicacion.excepciones.BusinessException;
import com.lachozag4.pisip.aplicacion.excepciones.NotFoundException;
import com.lachozag4.pisip.dominio.entidades.PedidoDetalle;
import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.dominio.repositorios.IProductoRepositorio;
import com.lachozag4.pisip.dominio.servicios.IGestionStockServicio;

import lombok.RequiredArgsConstructor;

/**
 * Implementación del servicio de dominio para gestión de stock.
 * 
 * SRP: Única responsabilidad → operaciones de stock sobre productos.
 * DIP: Depende de la abstracción IProductoRepositorio (puerto), no de la implementación.
 * OCP: Se puede extender sin modificar (ej. notificaciones de stock bajo).
 */
@RequiredArgsConstructor
public class GestionStockServicioImpl implements IGestionStockServicio {

    private final IProductoRepositorio productoRepositorio;

    @Override
    public void validarYDescontar(List<PedidoDetalle> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            return;
        }

        for (PedidoDetalle detalle : detalles) {
            Producto producto = obtenerProducto(detalle.getProducto().getIdproducto());

            if (!producto.hayStockDisponible(detalle.getCantidad())) {
                throw new BusinessException(
                    "Stock insuficiente para '" + producto.getNombre() + "'. " +
                    "Disponible: " + producto.getStockActual() + ", Solicitado: " + detalle.getCantidad());
            }

            Producto actualizado = producto.descontarStock(detalle.getCantidad());
            productoRepositorio.actualizar(actualizado);
        }
    }

    @Override
    public void restaurar(List<PedidoDetalle> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            return;
        }

        for (PedidoDetalle detalle : detalles) {
            productoRepositorio.buscarPorId(detalle.getProducto().getIdproducto())
                .ifPresent(producto -> {
                    Producto actualizado = producto.restaurarStock(detalle.getCantidad());
                    productoRepositorio.actualizar(actualizado);
                });
        }
    }

    @Override
    public void validarProductosActivos(List<PedidoDetalle> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            return;
        }

        for (PedidoDetalle detalle : detalles) {
            Producto producto = obtenerProducto(detalle.getProducto().getIdproducto());

            if (!producto.estaActivo()) {
                throw new BusinessException(
                    "El producto '" + producto.getNombre() + "' ya no está disponible");
            }
        }
    }

    private Producto obtenerProducto(int idProducto) {
        return productoRepositorio.buscarPorId(idProducto)
            .orElseThrow(() -> new NotFoundException("Producto no encontrado: " + idProducto));
    }
}
