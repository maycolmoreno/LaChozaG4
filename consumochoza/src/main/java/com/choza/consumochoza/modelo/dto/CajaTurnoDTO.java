package com.choza.consumochoza.modelo.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CajaTurnoDTO {

    private int idcaja;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private double montoInicial;
    private Double montoEsperadoCierre;
    private Double montoDeclaradoCierre;
    private Double diferencia;
    private String estado;
    private String usuarioApertura;
    private String usuarioCierre;
    private String observaciones;
}
