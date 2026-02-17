package com.lachozag4.pisip.presentacion.dto.response;

import lombok.Data;

@Data
public class ClienteResponseDTO {

	private int idcliente;
	private String nombre;
	private String cedula;
	private String telefono;
	private String direccion;
	private String email;
	private boolean estado;

	

}
