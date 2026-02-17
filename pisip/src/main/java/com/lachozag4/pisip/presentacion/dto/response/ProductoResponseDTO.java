package com.lachozag4.pisip.presentacion.dto.response;

import lombok.Data;

@Data
public class ProductoResponseDTO {

	private int idproducto;
	private String nombre;
	private double precio;
	private int stockActual;
	private String descripcion;
	private String imagenUrl;
	private boolean estado;
	private int categoriaId;

}
