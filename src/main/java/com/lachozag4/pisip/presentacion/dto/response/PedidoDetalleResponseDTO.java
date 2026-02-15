package com.lachozag4.pisip.presentacion.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PedidoDetalleResponseDTO {

	private int idpedidodetalle;

	@JsonProperty("producto")
	private ProductoResponseDTO fkProducto;
	private int cantidad;
	private double precioUnitario;

	// ✅ Subtotal calculado
	private double subtotal;
}