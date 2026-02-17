package com.lachozag4.pisip.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoriaRequestDTO {
	@NotNull
	private int idcategoria;

	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 255, message = "El nombre debe tener como máximo 255 caracteres")
	private String nombre;

	private boolean estado;
	
	@NotBlank(message = "La descripción es obligatoria")
	@Size(max = 255, message = "La descripción debe tener como máximo 255 caracteres")
	private String descripcion;

	

}
