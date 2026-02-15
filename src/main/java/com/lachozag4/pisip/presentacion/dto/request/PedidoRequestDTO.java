package com.lachozag4.pisip.presentacion.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PedidoRequestDTO {

	@NotNull(message = "La fecha es obligatoria")
	private LocalDateTime fecha;

	@NotNull(message = "El estado es obligatorio")
	private String estado;

	@Size(max = 500, message = "Las observaciones no deben superar 500 caracteres")
	private String observaciones;

	@Min(value = 1, message = "El ID del usuario es obligatorio")
	private int idUsuario;

	@Min(value = 1, message = "El ID de la mesa es obligatorio")
	private int idMesa;

	@Min(value = 1, message = "El ID del cliente es obligatorio")
	private int idCliente;

	@NotNull(message = "El pedido debe contener al menos un detalle")
	@Size(min = 1, message = "El pedido debe contener al menos un detalle")
	private List<PedidoDetalleRequestDTO> detalles;
}
