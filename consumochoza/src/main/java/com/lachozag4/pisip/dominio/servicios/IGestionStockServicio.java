package com.lachozag4.pisip.dominio.servicios;

import java.util.List;

import com.lachozag4.pisip.dominio.entidades.PedidoDetalle;

/**
 * Puerto de servicio de dominio para la gestión de stock de productos.
 * 
 * Responsabilidad única (SRP): centraliza las operaciones de stock
 * para ser reutilizado por distintos casos de uso sin duplicar lógica.
 */
public interface IGestionStockServicio {

    /**
     * Valida que haya stock suficiente y descuenta las cantidades de cada detalle.
     *
     * @param detalles lista de detalles del pedido con producto y cantidad
     * @throws com.lachozag4.pisip.aplicacion.excepciones.NotFoundException si un producto no existe
     * @throws com.lachozag4.pisip.aplicacion.excepciones.BusinessException si no hay stock suficiente
     */
    void validarYDescontar(List<PedidoDetalle> detalles);

    /**
     * Restaura (devuelve) el stock de las cantidades indicadas en cada detalle.
     *
     * @param detalles lista de detalles cuyo stock se debe restaurar
     */
    void restaurar(List<PedidoDetalle> detalles);

    /**
     * Valida que todos los productos de los detalles sigan activos.
     *
     * @param detalles lista de detalles a verificar
     * @throws com.lachozag4.pisip.aplicacion.excepciones.BusinessException si un producto no está activo
     */
    void validarProductosActivos(List<PedidoDetalle> detalles);
}
