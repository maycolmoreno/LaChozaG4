package com.lachozag4.pisip.presentacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private int idusuario;
    private String username;
    private String nombreCompleto;
    private String rol;
    private boolean requiereCambioPassword;
}
