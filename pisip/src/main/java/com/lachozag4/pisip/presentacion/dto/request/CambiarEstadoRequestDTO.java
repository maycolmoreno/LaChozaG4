package com.lachozag4.pisip.presentacion.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CambiarEstadoRequestDTO {

	@NotNull(message = "El estado es obligatorio")
	private String estado;
}
