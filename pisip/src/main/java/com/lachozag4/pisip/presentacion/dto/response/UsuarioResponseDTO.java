package com.lachozag4.pisip.presentacion.dto.response;

import lombok.Data;

@Data
public class UsuarioResponseDTO {
	private int idusuario;
	private String username;
	private String nombreCompleto;
	private String rol;
	private boolean estado;
	private boolean requiereCambioPassword;
}
