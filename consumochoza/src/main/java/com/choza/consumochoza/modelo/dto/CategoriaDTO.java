package com.choza.consumochoza.modelo.dto;

import lombok.Data;

@Data
public class CategoriaDTO {
    private int idcategoria;
    private String nombre;
    private String descripcion;
    private boolean estado;
}
