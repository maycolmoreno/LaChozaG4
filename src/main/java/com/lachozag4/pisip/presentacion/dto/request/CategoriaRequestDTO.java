package com.lachozag4.pisip.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoriaRequestDTO {
	@NotBlank
	private  int idcategoria;
	@NotBlank
    private  String nombre;
	@NotBlank
    private  String descripcion;
	@NotBlank
    private  Boolean  activo = true;

}
