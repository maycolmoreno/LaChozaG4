package com.lachozag4.pisip.presentacion.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CuentaResponseDTO {

	private int idcuenta;
	private LocalDateTime fechaApertura;
	private LocalDateTime fechaCierre;
	private String estado;
	private double total;

	@JsonProperty("mesa")
	private MesaResponseDTO fkMesa;

	@JsonProperty("cliente")
	private ClienteResponseDTO fkCliente;
}
