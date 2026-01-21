package com.lachozag4.pisip.presentacion.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MesaRequestDTO {
	
	private int idmesa;
    @NotNull(message = "El número de mesa es obligatorio")
    @Min(value = 1, message = "El número debe ser mayor a 0")
	private Integer numero;
    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    @Max(value = 50, message = "La capacidad no puede superar 50 personas")
	private Integer capacidad;
    @NotNull(message = "El estado es obligatorio")
	private boolean estado; // "LIBRE", "OCUPADA"
    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 100, message = "La ubicación no debe superar 100 caracteres")
    private String ubicacion; // "Salon 1", "Salon 2"

}
