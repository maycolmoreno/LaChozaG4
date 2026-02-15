package com.choza.consumochoza.modelo.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private int idusuario;
    private String username;
    private String password; // Solo para crear/editar
    private String nombreCompleto;
    private String rol; // ADMIN, CAMARERO, COCINA
    private boolean estado;
    private boolean requiereCambioPassword;
}
