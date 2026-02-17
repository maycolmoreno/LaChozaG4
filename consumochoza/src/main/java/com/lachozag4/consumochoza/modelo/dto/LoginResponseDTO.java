package com.lachozag4.consumochoza.modelo.dto;

import lombok.Data;

/**
 * DTO para recibir la respuesta de login de la API (pisip).
 * Contiene el JWT token y los datos bÃ¡sicos del usuario.
 */
@Data
public class LoginResponseDTO {
    private String token;
    private int idusuario;
    private String username;
    private String nombreCompleto;
    private String rol;
    private boolean requiereCambioPassword;
}

