package com.lachozag4.pisip.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductoRequestDTO {
	@NotBlank
	private int idproducto;
	@NotBlank
	private String nombre;
	@NotBlank
	private Double precio;
	@NotBlank
	private Integer stockActual;
	@NotBlank
	private String descripcion;
	@NotBlank
	private boolean estado;

}
