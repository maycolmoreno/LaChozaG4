package com.lachozag4.pisip.presentacion.dto.response;

import java.time.LocalDateTime;

/**
 * Payload enviado a través de STOMP a los topics /topic/cocina y /topic/camarero.
 */
public class NotificacionPedidoDTO {

    private int     pedidoId;
    private String  evento;       // CONFIRMAR | LISTO | ENTREGADO | CANCELADO
    private String  estadoNuevo;
    private String  mensaje;
    private String  emisor;       // rol o usuario que originó el cambio
    private LocalDateTime fecha;

    // ─── Constructor de conveniencia ───────────────────────────────────────────
    public static NotificacionPedidoDTO de(int pedidoId, String evento,
                                           String estadoNuevo, String emisor) {
        NotificacionPedidoDTO dto = new NotificacionPedidoDTO();
        dto.pedidoId   = pedidoId;
        dto.evento     = evento;
        dto.estadoNuevo = estadoNuevo;
        dto.emisor     = emisor;
        dto.fecha      = LocalDateTime.now();
        dto.mensaje    = generarMensaje(evento, pedidoId);
        return dto;
    }

    private static String generarMensaje(String evento, int pedidoId) {
        return switch (evento) {
            case "CONFIRMAR"  -> "Pedido #" + pedidoId + " enviado a cocina";
            case "PREPARANDO" -> "Pedido #" + pedidoId + " en preparación";
            case "LISTO"      -> "Pedido #" + pedidoId + " listo para entregar";
            case "ENTREGADO"  -> "Pedido #" + pedidoId + " entregado al cliente";
            case "CANCELADO"  -> "Pedido #" + pedidoId + " cancelado";
            default           -> "Cambio de estado en pedido #" + pedidoId;
        };
    }

    // ─── Getters ───────────────────────────────────────────────────────────────
    public int     getPedidoId()    { return pedidoId; }
    public String  getEvento()      { return evento; }
    public String  getEstadoNuevo() { return estadoNuevo; }
    public String  getMensaje()     { return mensaje; }
    public String  getEmisor()      { return emisor; }
    public LocalDateTime getFecha() { return fecha; }
}
