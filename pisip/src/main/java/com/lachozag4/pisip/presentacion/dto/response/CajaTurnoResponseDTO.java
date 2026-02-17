package com.lachozag4.pisip.presentacion.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CajaTurnoResponseDTO {

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
