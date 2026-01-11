package com.lachozag4.pisip.presentacion.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClienteRequestDTO {
	
	private  int idcliente;

@NotBlank(message = "La identificación es obligatoria")
    @Size(min = 10, max = 13, message = "La identificación debe tener entre 10 y 13 caracteres")

	private  String identificacion; // Cédula o RUC

@NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no debe superar 120 caracteres")

	private  String nombre;

@NotBlank(message = "La dirección es obligatoria")
    @Size(max = 200, message = "La dirección no debe superar 200 caracteres")

	private  String direccion;

@NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "\\d{7,10}", message = "El teléfono debe contener entre 7 y 10 dígitos")

	private  String telefono;

@NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")

	private  String email;

}
