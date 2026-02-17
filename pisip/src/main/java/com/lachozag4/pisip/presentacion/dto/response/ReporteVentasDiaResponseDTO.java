package com.lachozag4.pisip.presentacion.dto.response;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ReporteVentasDiaResponseDTO {

    private LocalDate fecha;
    private double totalVentas;
    private int numeroPedidos;
    private double ticketPromedio;
    private int totalProductos;

    private List<PedidoResponseDTO> pedidos;
    private List<ResumenProductoVentaDTO> productos;
}
