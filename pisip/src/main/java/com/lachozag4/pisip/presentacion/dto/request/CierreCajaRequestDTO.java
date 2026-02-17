package com.lachozag4.pisip.presentacion.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CierreCajaRequestDTO {

	@DecimalMin(value = "0.0", inclusive = true, message = "El monto declarado debe ser >= 0")
	private double montoDeclaradoCierre;

	@NotBlank(message = "El usuario de cierre es obligatorio")
	private String usuarioCierre;

	private String observaciones;
}
