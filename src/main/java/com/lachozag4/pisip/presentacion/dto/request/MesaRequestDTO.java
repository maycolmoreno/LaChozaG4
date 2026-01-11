package com.lachozag4.pisip.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MesaRequestDTO {
	@NotBlank
	private int idmesa;
	@NotBlank
	private Integer numero;
	@NotBlank
	private Integer capacidad;
	@NotBlank
	private boolean estado; // "LIBRE", "OCUPADA"
	@NotBlank
	private String ubicacion; // "Salon 1", "Salon 2"

}
