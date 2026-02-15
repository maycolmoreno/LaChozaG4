package com.lachozag4.pisip.aplicacion.casosuso.impl;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IPedidoDetalleUseCase;
import com.lachozag4.pisip.aplicacion.excepciones.BusinessException;
import com.lachozag4.pisip.aplicacion.excepciones.NotFoundException;
import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.dominio.entidades.PedidoDetalle;
import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.dominio.repositorios.IPedidoDetalleRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IPedidoRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IProductoRepositorio;
import com.lachozag4.pisip.dominio.servicios.IGestionStockServicio;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional // por defecto para comandos; ajusto lecturas abajo
public class PedidoDetalleUseCaseImpl implements IPedidoDetalleUseCase {

    private final IPedidoDetalleRepositorio detalleRepo;
    private final IPedidoRepositorio pedidoRepo;
    private final IProductoRepositorio productoRepo;
    private final IGestionStockServicio stockServicio;

    /* ========================
       Lecturas
       ======================== */

    @Override
    @Transactional(readOnly = true)
    public List<PedidoDetalle> listarPorPedido(int idPedido) {
        verificarPedidoExiste(idPedido); // opcional: quítalo si prefieres 200 + []
        return detalleRepo.listarPorPedido(idPedido);
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoDetalle obtenerPorId(int idPedido, int idDetalle) {
        var det = detalleRepo.buscarPorId(idDetalle)
                .orElseThrow(() -> new NotFoundException("Detalle no encontrado: " + idDetalle));
        if (det.getFkPedido() == null || det.getFkPedido().getIdpedido() != idPedido) {
            throw new NotFoundException("El detalle " + idDetalle + " no pertenece al pedido " + idPedido);
        }
        return det;
    }

    /* ========================
       Escrituras
       ======================== */

    @Override
    public PedidoDetalle crear(int idPedido, int idProducto, int cantidad, double precioUnitario) {
        validarCantidadYPrecio(cantidad, precioUnitario);
        var pedido = verificarPedidoEditable(idPedido);
        var producto = verificarProductoActivoConStock(idProducto, cantidad);

        // Construye detalle (id = 0 para crear)
        var detalle = new PedidoDetalle(
                0,
                producto,
                pedido,
                cantidad,
                precioUnitario
        );

        // Descuenta stock y guarda (si prefieres, haz el descuento después del guardar)
        stockServicio.validarYDescontar(List.of(detalle));
        return detalleRepo.guardar(detalle);
    }

    @Override
    public PedidoDetalle actualizar(int idPedido, int idDetalle, int idProducto, int cantidad, double precioUnitario) {
        validarCantidadYPrecio(cantidad, precioUnitario);
        var pedido = verificarPedidoEditable(idPedido);

        var actual = obtenerPorId(idPedido, idDetalle);
        if (actual.getFkProducto() == null) {
            throw new BusinessException("El detalle no tiene producto asociado.");
        }

        // 1) Restaurar stock previo (producto anterior)
        stockServicio.restaurar(List.of(actual));

        // 2) Verificar nuevo producto y stock, y descontar
        var nuevoProducto = verificarProductoActivoConStock(idProducto, cantidad);
        var actualizado = new PedidoDetalle(
                actual.getIdpedidodetalle(),
                nuevoProducto,
                pedido,
                cantidad,
                precioUnitario
        );
        stockServicio.validarYDescontar(List.of(actualizado));

        // 3) Persistir cambios
        return detalleRepo.guardar(actualizado);
    }

    @Override
    public void eliminar(int idPedido, int idDetalle) {
        var det = obtenerPorId(idPedido, idDetalle);
        verificarPedidoEditable(idPedido);

        // 1) Restaurar stock
        stockServicio.restaurar(List.of(det));

        // 2) Eliminar
        detalleRepo.eliminar(idDetalle);
    }

    /* ========================
       Helpers
       ======================== */

    private void validarCantidadYPrecio(int cantidad, double precioUnitario) {
        if (cantidad <= 0) throw new BusinessException("Cantidad inválida (debe ser > 0).");
        if (precioUnitario <= 0) throw new BusinessException("Precio unitario inválido (debe ser > 0).");
    }

    private Pedido verificarPedidoExiste(int idPedido) {
        return pedidoRepo.buscarPorId(idPedido)
                .orElseThrow(() -> new NotFoundException("Pedido no encontrado: " + idPedido));
    }

    private Pedido verificarPedidoEditable(int idPedido) {
        var pedido = verificarPedidoExiste(idPedido);
        if (!pedido.esEditable()) {
            throw new BusinessException("El pedido #" + idPedido + " no está en estado editable (PENDIENTE).");
        }
        return pedido;
    }

    private Producto verificarProductoActivoConStock(int idProducto, int cantidad) {
        var producto = productoRepo.buscarPorId(idProducto)
                .orElseThrow(() -> new NotFoundException("Producto no encontrado: " + idProducto));
        if (!producto.estaActivo()) {
            throw new BusinessException("El producto " + idProducto + " no está activo.");
        }
        if (!producto.hayStockDisponible(cantidad)) {
            throw new BusinessException("Stock insuficiente. Requerido: " + cantidad);
        }
        return producto;
    }
}