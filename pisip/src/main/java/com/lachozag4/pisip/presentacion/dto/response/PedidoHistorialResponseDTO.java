package com.lachozag4.pisip.presentacion.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PedidoHistorialResponseDTO {

    private int idhistorial;
    private int idpedido;
    private String accion;
    private String estadoAnterior;
    private String estadoNuevo;
    private Integer idusuario;
    private String usuarioNombre;
    private String usuarioRol;
    private LocalDateTime fecha;
    private String observacion;
}
