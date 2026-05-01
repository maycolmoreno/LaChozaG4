package com.choza.consumochoza.modelo.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PagoDTO {

    private int idpago;
    private LocalDateTime fecha;
    private double monto;
    private String metodo;
    private String referencia;
    private String usuario;
    private int idcuenta;
    private int idcaja;
    private double totalPagadoCuenta;
    private double saldoPendienteCuenta;
}
