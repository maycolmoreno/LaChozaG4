package com.choza.consumochoza.modelo.dto;

import lombok.Data;

@Data
public class CambiarPasswordDTO {

    private String passwordActual;
    private String passwordNuevo;
    private String passwordConfirmacion;
}
