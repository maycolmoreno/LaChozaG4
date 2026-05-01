package com.choza.consumochoza.modelo.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ReporteVentasDiaDTO {

    private LocalDate fecha;
    private double totalVentas;
    private int numeroPedidos;
    private double ticketPromedio;
    private int totalProductos;

    private List<PedidoDTO> pedidos;
    private List<ResumenProductoVentaDTO> productos;
}
