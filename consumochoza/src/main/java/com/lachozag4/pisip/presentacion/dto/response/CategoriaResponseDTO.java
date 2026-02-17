package com.lachozag4.pisip.presentacion.dto.response;

import lombok.Data;

@Data
public class CategoriaResponseDTO {
	private int idcategoria;
	private String nombre;
	private String descripcion;
	private boolean estado;
	
}
