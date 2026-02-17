package com.lachozag4.pisip.presentacion.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CuentaRequestDTO {

	@NotNull(message = "El ID de la mesa es obligatorio")
	@Min(value = 1, message = "El ID de la mesa es obligatorio")
	private Integer idMesa;

	@NotNull(message = "El ID del cliente es obligatorio")
	@Min(value = 1, message = "El ID del cliente es obligatorio")
	private Integer idCliente;

	// El total inicial puede ser 0; se actualizará luego según pedidos
	private double total;
}
