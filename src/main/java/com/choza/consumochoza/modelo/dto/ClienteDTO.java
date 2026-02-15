package com.choza.consumochoza.modelo.dto;

import lombok.Data;

@Data
public class ClienteDTO {
    private int idcliente;
    private String nombre;
    private String cedula;
    private String telefono;
    private String direccion;
    private String email;
    private boolean estado;
}
