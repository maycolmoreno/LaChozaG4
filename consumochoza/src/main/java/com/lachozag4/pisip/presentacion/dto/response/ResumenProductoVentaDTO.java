package com.lachozag4.pisip.presentacion.dto.response;

import lombok.Data;

@Data
public class ResumenProductoVentaDTO {

    private int idProducto;
    private String nombreProducto;
    private int cantidadVendida;
    private double totalVendido;
}
