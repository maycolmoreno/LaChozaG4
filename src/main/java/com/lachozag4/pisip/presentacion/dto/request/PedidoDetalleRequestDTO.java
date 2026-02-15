package com.lachozag4.pisip.presentacion.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PedidoDetalleRequestDTO {

	private int idpedidodetalle;

	// ID del producto (coincide con lo que envía el front consumochoza)
	@Min(value = 1, message = "El ID del producto es obligatorio")
	private int idProducto;

	@Min(value = 1, message = "La cantidad debe ser al menos 1")
	private int cantidad;

	@DecimalMin(value = "0.0", inclusive = true, message = "Precio debe ser >= 0")
	private double precioUnitario;

	private double subtotal;

}