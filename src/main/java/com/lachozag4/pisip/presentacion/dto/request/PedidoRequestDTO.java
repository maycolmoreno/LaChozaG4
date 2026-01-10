package com.lachozag4.pisip.presentacion.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PedidoRequestDTO {
	@NotBlank
	private int idpedido;
	@NotBlank
	private LocalDateTime fecha = LocalDateTime.now();
	@NotBlank
	private boolean estado;
	@NotBlank
	private String observaciones;

}
