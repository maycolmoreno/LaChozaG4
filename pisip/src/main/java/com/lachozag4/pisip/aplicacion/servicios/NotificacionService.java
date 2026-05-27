package com.lachozag4.pisip.aplicacion.servicios;

import com.lachozag4.pisip.presentacion.dto.response.NotificacionPedidoDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Servicio de notificaciones en tiempo real.
 * Publica mensajes STOMP a los topics según el rol destinatario.
 *
 * Topics:
 *   /topic/cocina    → reciben el personal de cocina
 *   /topic/camarero  → reciben los camareros
 */
@Service
public class NotificacionService {

    private static final String TOPIC_COCINA   = "/topic/cocina";
    private static final String TOPIC_CAMARERO = "/topic/camarero";

    private final SimpMessagingTemplate messaging;

    public NotificacionService(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    /**
     * Notifica a la cocina que hay un nuevo pedido para preparar.
     * Disparado cuando el camarero confirma el pedido.
     */
    public void notificarCocina(int pedidoId, String estadoNuevo, String emisor) {
        var dto = NotificacionPedidoDTO.de(pedidoId, "CONFIRMAR", estadoNuevo, emisor);
        messaging.convertAndSend(TOPIC_COCINA, dto);
    }

    /**
     * Notifica a la cocina que el pedido pasó a estado EN_COCINA directamente.
     */
    public void notificarCocinaPrepara(int pedidoId, String estadoNuevo, String emisor) {
        var dto = NotificacionPedidoDTO.de(pedidoId, "PREPARANDO", estadoNuevo, emisor);
        messaging.convertAndSend(TOPIC_COCINA, dto);
    }

    /**
     * Notifica a los camareros que un pedido está listo para entregar.
     * Disparado por la cocina.
     */
    public void notificarCamareroListo(int pedidoId, String estadoNuevo, String emisor) {
        var dto = NotificacionPedidoDTO.de(pedidoId, "LISTO", estadoNuevo, emisor);
        messaging.convertAndSend(TOPIC_CAMARERO, dto);
    }

    /**
     * Notifica a los camareros que el pedido fue entregado.
     */
    public void notificarCamareroEntregado(int pedidoId, String estadoNuevo, String emisor) {
        var dto = NotificacionPedidoDTO.de(pedidoId, "ENTREGADO", estadoNuevo, emisor);
        messaging.convertAndSend(TOPIC_CAMARERO, dto);
    }

    /**
     * Notifica a cocina y camarero que el pedido fue cancelado.
     */
    public void notificarCancelado(int pedidoId, String estadoNuevo, String emisor) {
        var dto = NotificacionPedidoDTO.de(pedidoId, "CANCELADO", estadoNuevo, emisor);
        messaging.convertAndSend(TOPIC_COCINA,   dto);
        messaging.convertAndSend(TOPIC_CAMARERO, dto);
    }
}
