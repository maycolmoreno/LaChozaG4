package com.lachozag4.pisip.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PedidoDetalleRequestDTO {
	@NotBlank
	private int idpedidodetalle;
	@NotBlank
	private Integer cantidad; // >= 1

}
