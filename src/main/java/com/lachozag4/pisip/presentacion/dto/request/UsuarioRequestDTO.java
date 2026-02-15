package com.lachozag4.pisip.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioRequestDTO {
	
	private int idusuario;
	@NotBlank(message = "El username es obligatorio")
	private String username;
	@NotBlank(message = "La contraseña es obligatoria")
	private String password;	
	@NotBlank(message = "El nombre completo es obligatorio")
	private String nombreCompleto;	
	@NotBlank(message = "El rol es obligatorio")
	private String rol; // EJEMPLOS: 'ADMIN', 'CAMARERO', 'COCINA'
	private boolean estado;
	private boolean requiereCambioPassword;

}
