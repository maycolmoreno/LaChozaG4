package com.choza.consumochoza.modelo.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class PedidoDTO {
    private int idpedido;
    private LocalDateTime fecha;
    private String estado;
    private String observaciones;
    
    // Relaciones completas (respuesta)
    private UsuarioDTO usuario;
    private MesaDTO mesa;
    private ClienteDTO cliente;
    private List<PedidoDetalleDTO> detalle;
    
    // Campos calculados
    private double total;
    private int cantidadProductos;
    
    // Para crear pedido (IDs) - coinciden con API
    private int idUsuario;
    private int idMesa;
    private int idCliente;
    private List<PedidoDetalleDTO> detalles;
}
