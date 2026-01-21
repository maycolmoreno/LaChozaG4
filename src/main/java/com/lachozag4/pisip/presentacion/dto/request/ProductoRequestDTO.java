package com.lachozag4.pisip.presentacion.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductoRequestDTO {

	private int idproducto;
	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 200, message = "El nombre no debe superar 200 caracteres")
	private String nombre;
	@NotNull(message = "El precio es obligatorio")
	@DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
	@DecimalMax(value = "99.99", message = "El precio es demasiado alto")
	private Double precio;
	@NotNull(message = "El stock de producti tiene que ingresar ")
	@Min(value = 1, message = "Mínimo debe ser al menos 1 producto")
	@Max(value = 50, message = "Máximo tiene que ser 20 producto")
	private Integer stockActual;
	@Size(max = 500, message = "La descripción no debe superar 500 caracteres")
	private String descripcion;
	@NotNull(message = "El estado es obligatorio")
	private boolean estado;

}
