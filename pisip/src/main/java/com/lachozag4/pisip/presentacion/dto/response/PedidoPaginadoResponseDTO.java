package com.lachozag4.pisip.presentacion.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para listados paginados de pedidos.
 * Compatible con PedidosPaginadosDTO del frontend consumochoza.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoPaginadoResponseDTO {

	private List<PedidoResponseDTO> pedidos;
	private long totalPedidos;
	private int totalPaginas;
	private int paginaActual;
	private int tamanioPagina;
}
