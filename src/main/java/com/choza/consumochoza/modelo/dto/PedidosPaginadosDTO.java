package com.choza.consumochoza.modelo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que encapsula el resultado de una consulta paginada de pedidos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidosPaginadosDTO {
    
    private List<PedidoDTO> pedidos;
    private int totalPedidos;
    private int totalPaginas;
    private int paginaActual;
    private int tamanioPagina;
}
