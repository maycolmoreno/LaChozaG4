package com.lachozag4.pisip.aplicacion.casosuso.entradas;

import com.lachozag4.pisip.dominio.entidades.Pedido;
import java.util.List;

public interface GestionarCocinaPort {
    List<Pedido> listarPedidosPendientes();
    void cambiarEstadoAPreparando(Long pedidoId);
    void marcarComoListo(Long pedidoId);
}