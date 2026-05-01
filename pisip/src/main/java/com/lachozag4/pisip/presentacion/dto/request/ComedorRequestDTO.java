package com.lachozag4.pisip.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ComedorRequestDTO {

	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 100, message = "El nombre debe tener como máximo 100 caracteres")
	private String nombre;

	@Size(max = 500, message = "La descripción debe tener como máximo 500 caracteres")
	private String descripcion;

	private boolean estado;
}
