package com.lachozag4.pisip.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClienteRequestDTO {
	@NotBlank
	private  int idcliente;
	@NotBlank
	private  String identificacion; // Cédula o RUC
	@NotBlank
	private  String nombre;
	@NotBlank
	private  String direccion;
	@NotBlank
	private  String telefono;
	@NotBlank
	private  String email;

}
