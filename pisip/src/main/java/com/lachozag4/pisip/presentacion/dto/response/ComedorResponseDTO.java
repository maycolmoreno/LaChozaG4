package com.lachozag4.pisip.presentacion.dto.response;

import lombok.Data;

@Data
public class ComedorResponseDTO {

	private int idcomedor;
	private String nombre;
	private String descripcion;
	private boolean estado;
}
