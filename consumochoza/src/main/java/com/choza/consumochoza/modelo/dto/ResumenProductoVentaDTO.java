package com.choza.consumochoza.modelo.dto;

import lombok.Data;

@Data
public class ResumenProductoVentaDTO {

    private int idProducto;
    private String nombreProducto;
    private int cantidadVendida;
    private double totalVendido;
}
