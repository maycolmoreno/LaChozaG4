package com.lachozag4.pisip.presentacion.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PedidoRequestDTO {

	private int idpedido;

	private LocalDateTime fecha;
	@NotNull(message = "El estado es obligatorio")
	private boolean estado;
	@Size(max = 500, message = "Las observaciones no deben superar 500 caracteres")
	private String observaciones;

	private UsuarioRequestDTO FkUsuario;

	private MesaRequestDTO Fkmesa;
	private ClienteRequestDTO Fkcliente;

}
