package com.lachozag4.pisip.presentacion.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PagoRequestDTO {

	@DecimalMin(value = "0.01", inclusive = true, message = "El monto debe ser mayor a 0")
	private double monto;

	@NotBlank(message = "El método de pago es obligatorio")
	private String metodo;

	private String referencia;

	@NotBlank(message = "El usuario que registra el pago es obligatorio")
	private String usuario;
}
