package com.lachozag4.consumochoza.modelo.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CuentaDTO {

    private int idcuenta;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private String estado;
    private double total;

    private MesaDTO mesa;
    private ClienteDTO cliente;
}

