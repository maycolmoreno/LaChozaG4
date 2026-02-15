package com.lachozag4.pisip.presentacion.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductoRequestDTO {

	private int idproducto;
	@NotBlank(message = "El nombre es obligatorio")
	@Size(max = 200, message = "El nombre no debe superar 200 caracteres")
	private String nombre;
	@DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
	@DecimalMax(value = "99.99", message = "El precio es demasiado alto")
	private double precio;
	@Min(value = 1, message = "Mínimo debe ser al menos 1 producto")
	@Max(value = 50, message = "Máximo debe ser 50 productos")
	private int stockActual;
	@Size(max = 500, message = "La descripción no debe superar 500 caracteres")
	private String descripcion;
	@Size(max = 500, message = "La URL de imagen no debe superar 500 caracteres")
	private String imagenUrl;
	private boolean estado;

	@Min(value = 1, message = "La categoría es obligatoria")
	private int categoriaId;

}
