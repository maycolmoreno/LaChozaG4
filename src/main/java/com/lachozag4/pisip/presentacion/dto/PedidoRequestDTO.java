package com.lachozag4.pisip.presentacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequestDTO {

    private Long mesaId;      // ID de la mesa donde se sientan los clientes
    private Long clienteId;   // ID del cliente (para la factura)
    private List<ItemPedidoDTO> items; // Lista de ceviches y cantidades

    /**
     * Clase interna para representar cada línea del pedido
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemPedidoDTO {
        private Long productoId; // ID del Ceviche
        private Integer cantidad; // ¿Cuántos platos de este tipo?
    }
}