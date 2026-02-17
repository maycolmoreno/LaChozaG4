package com.lachozag4.pisip.dominio.entidades;

import java.io.Serializable;

public class PedidoDetalle implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int idpedidodetalle;
	private Producto fkProducto;
	private Pedido fkPedido; // ✅ Solo ID, no objeto completo (evita ciclos)
	private final int cantidad;
	private final double precioUnitario;

	public PedidoDetalle(int idpedidodetalle, Producto fkProducto, Pedido fkPedido, int cantidad,
			double precioUnitario) {
		this.idpedidodetalle = idpedidodetalle;
		this.fkProducto = fkProducto;
		this.fkPedido = fkPedido;
		this.cantidad = cantidad;
		this.precioUnitario = precioUnitario;
	}

	// Getters
	public int getIdpedidodetalle() {
		return idpedidodetalle;
	}

	public Producto getProducto() {
		return fkProducto;
	}

	public Producto getFkProducto() {
		return fkProducto;
	}

	public void setFkProducto(Producto fkProducto) {
		this.fkProducto = fkProducto;
	}

	public Pedido getFkPedido() {
		return fkPedido;
	}

	public void setFkPedido(Pedido fkPedido) {
		this.fkPedido = fkPedido;
	}

	public int getCantidad() {
		return cantidad;
	}

	public double getPrecioUnitario() {
		return precioUnitario;
	}

	// ✅ Métodos de negocio
	public double calcularSubtotal() {
		return cantidad * precioUnitario;
	}

	@Override
	public String toString() {
		return "PedidoDetalle [idpedidodetalle=" + idpedidodetalle + ", fkProducto=" + fkProducto + ", fkPedido="
				+ fkPedido + ", cantidad=" + cantidad + ", precioUnitario=" + precioUnitario + "]";
	}

}