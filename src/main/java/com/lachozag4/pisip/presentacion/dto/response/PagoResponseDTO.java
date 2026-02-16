package com.lachozag4.pisip.presentacion.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PagoResponseDTO {

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
