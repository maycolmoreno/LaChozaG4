package com.choza.consumochoza.modelo.dto;

import lombok.Data;

@Data
public class ProductoDTO {
    private int idproducto;
    private String nombre;
    private double precio;
    private int stockActual;
    private String descripcion;
    private String imagenUrl;
    private boolean estado;
    private int categoriaId;
}
