package com.lachozag4.pisip.dominio.enums;

/**
 * Estados posibles de un pedido.
 * El valor almacenado en BD es el nombre del enum (EnumType.STRING).
 */
public enum EstadoPedido {
    PENDIENTE,
    EN_COCINA,
    EN_BAR,
    LISTO_PARA_ENTREGA,
    COMPLETADO,
    CANCELADO
}
