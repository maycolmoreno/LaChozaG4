package com.lachozag4.pisip.presentacion.dto.response;

public class PedidoDetalleResponseDTO {
	private int idpedidodetalle;
	private Integer cantidad; // >= 1

	public int getIdpedidodetalle() {
		return idpedidodetalle;
	}

	public void setIdpedidodetalle(int idpedidodetalle) {
		this.idpedidodetalle = idpedidodetalle;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

}
