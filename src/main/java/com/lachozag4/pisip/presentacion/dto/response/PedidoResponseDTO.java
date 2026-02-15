package com.lachozag4.pisip.presentacion.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * DTO de respuesta para Pedido
 * 
 * CAMBIOS:
 * - ✅ Incluye lista de detalles
 * - ✅ Incluye objetos relacionados completos
 * - ✅ Campos calculados (total, cantidad productos)
 */
@Data
public class PedidoResponseDTO {
    
    private int idpedido;
    private LocalDateTime fecha;
    private String estado;
    private String observaciones;
    
    // Relaciones (expuestas al cliente como usuario/mesa/cliente)
    @JsonProperty("usuario")
    private UsuarioResponseDTO fkUsuario;

    @JsonProperty("mesa")
    private MesaResponseDTO fkMesa;

    @JsonProperty("cliente")
    private ClienteResponseDTO fkCliente;
    
    // ✅ Lista de detalles
    private List<PedidoDetalleResponseDTO> detalle;
    
    // ✅ Campos calculados
    private double total;
    private int cantidadProductos;
}