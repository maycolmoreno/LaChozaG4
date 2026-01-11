package com.lachozag4.pisip.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioRequestDTO {
	@NotBlank
	private int idusuario;	
	@NotBlank
	private String username;
	@NotBlank
	private String password;	
	@NotBlank
	private String nombreCompleto;	
	@NotBlank
	private String rol; // EJEMPLOS: 'ADMIN', 'CAMARERO', 'COCINA'
	@NotBlank
	private boolean estado;

}
