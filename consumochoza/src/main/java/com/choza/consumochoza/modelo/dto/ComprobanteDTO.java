package com.choza.consumochoza.modelo.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ComprobanteDTO {

    private int idcomprobante;
    private int idpago;
    private String nombreArchivo;
    private String urlDescarga;
    private String contentType;
    private long tamano;
    private String usuarioRegistro;
    private LocalDateTime fechaSubida;
}