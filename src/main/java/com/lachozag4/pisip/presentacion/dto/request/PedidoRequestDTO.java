package com.lachozag4.pisip.presentacion.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PedidoRequestDTO {
	@NotBlank
	private int idpedido;
	@NotBlank
	private LocalDateTime fecha = LocalDateTime.now();
    @NotNull(message = "El estado es obligatorio")
	private boolean estado;
    @Size(max = 500, message = "Las observaciones no deben superar 500 caracteres")
	private String observaciones;
	@NotNull(message = "El cliente es obligatorio")
	private Integer idCliente;
	@NotNull(message = "La mesa es obligatoria")
	private Integer idMesa;

	@NotNull(message = "El usuario es obligatorio")
	private Integer idUsuario;

}
