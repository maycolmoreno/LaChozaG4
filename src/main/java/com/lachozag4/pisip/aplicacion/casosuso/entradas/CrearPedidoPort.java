package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import com.lachozag4.pisip.dominio.entidades.Pedido;

public interface CrearPedidoPort {
    /**
     * Procesa un nuevo pedido, valida stock de los 20 ceviches 
     * y calcula el IVA administrable.
     */
    Pedido ejecutar(Pedido pedido);
}