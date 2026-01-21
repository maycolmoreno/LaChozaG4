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
	@Size(max = 100, message = "El nombre no debe superar 100 caracteres")
	private String nombre;

	@NotBlank(message = "La descripción es obligatoria")
	@Size(max = 500, message = "La descripción no debe superar 500 caracteres")
	private String descripcion;
	
	@NotNull(message = "El estado es obligatorio")
	private Boolean estado;

}
