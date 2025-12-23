package com.lachozag4.pisip.dominio.entidades;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

	private Long id;
	private Cliente cliente;
	private Mesa mesa;
	private LocalDateTime fecha;
	private List<PedidoDetalle> detalles = new ArrayList<>();
	private Double total;
	private Double iva;
	private String estado; // "PENDIENTE", "PAGADO", "CANCELADO"

	/**
	 * Lógica de Negocio: Calcula el subtotal, el IVA y el total.
	 * 
	 * @param porcentajeIva Valor administrable (ej: 15.0 para Ecuador)
	 */
	public void calcularTotales(Double porcentajeIva) {
		double subtotal = detalles.stream().mapToDouble(PedidoDetalle::calcularSubtotal).sum();

		this.iva = subtotal * (porcentajeIva / 100);
		this.total = subtotal + this.iva;
	}

	/**
	 * Lógica de Negocio: Agrega un plato al pedido.
	 */
	public void agregarDetalle(PedidoDetalle detalle) {
		this.detalles.add(detalle);
	}
}