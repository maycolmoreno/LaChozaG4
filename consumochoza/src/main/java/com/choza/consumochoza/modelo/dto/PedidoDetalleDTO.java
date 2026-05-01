package com.choza.consumochoza.modelo.dto;

import lombok.Data;

@Data
public class PedidoDetalleDTO {
    private int idpedidodetalle;
    private int cantidad;
    private double precioUnitario;
    private ProductoDTO producto;
    private int idPedido;
    private double subtotal;
    
    // Para crear pedido
    private int idProducto;
}
