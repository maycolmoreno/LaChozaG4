package com.lachozag4.pisip.presentacion.dto.response;

import lombok.Data;

@Data
public class SaldoCuentaResponseDTO {

	private int idcuenta;
	private double totalPagado;
	private double saldoPendiente;
}