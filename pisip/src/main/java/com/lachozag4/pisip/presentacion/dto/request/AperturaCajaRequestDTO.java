package com.lachozag4.pisip.presentacion.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AperturaCajaRequestDTO {

	@DecimalMin(value = "0.0", inclusive = true, message = "El monto inicial debe ser >= 0")
	private double montoInicial;

	@NotBlank(message = "El usuario de apertura es obligatorio")
	private String usuarioApertura;

	private String observaciones;
}
