package com.lachozag4.pisip.presentacion.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ClienteRequestDTO {

	@NotBlank
	@Size(max = 255, message = "El nombre no puede tener más de 255 caracteres")
	private String nombre;

	@NotBlank(message = "La cédula es obligatoria")
	@Size(min = 10, max = 13, message = "La cédula debe tener entre 10 y 13 caracteres")
	private String cedula;

	@Pattern(regexp = "^$|^[0-9]{10}$", message = "El teléfono debe tener exactamente 10 dígitos numéricos")
	private String telefono;

	@Size(max = 255, message = "La dirección no puede tener más de 255 caracteres")
	private String direccion;

	@Email(message = "El email debe tener un formato válido")
	@Size(max = 150, message = "El email no puede tener más de 150 caracteres")
	private String email;

	private boolean estado;

}
