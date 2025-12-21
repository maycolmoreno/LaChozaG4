package com.lachozag4.pisip.aplicacion.casosuso.impl;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.CrearPedidoPort;
import com.lachozag4.pisip.aplicacion.excepciones.MesaOcupadaException;
import com.lachozag4.pisip.aplicacion.excepciones.StockInsuficienteException;
import com.lachozag4.pisip.aplicacion.puertos.salida.PedidoRepositoryPort;
import com.lachozag4.pisip.dominio.entidades.Pedido;

public class CrearPedidoUseCase implements CrearPedidoPort {

    private final PedidoRepositoryPort pedidoRepository;

    // 👇 CONSTRUCTOR EXPLÍCITO (CLAVE)
    public CrearPedidoUseCase(PedidoRepositoryPort pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public Pedido ejecutar(Pedido pedido) {

        if ("OCUPADA".equalsIgnoreCase(pedido.getMesa().getEstado())) {
            throw new MesaOcupadaException(pedido.getMesa().getNumero());
        }

        pedido.getDetalles().forEach(detalle -> {
            if (detalle.getProducto().getStockActual() < detalle.getCantidad()) {
                throw new StockInsuficienteException();
            }

            detalle.getProducto().setStockActual(
                detalle.getProducto().getStockActual() - detalle.getCantidad()
            );
        });

        pedido.getMesa().setEstado("OCUPADA");

        return pedidoRepository.guardar(pedido);
    }
}
