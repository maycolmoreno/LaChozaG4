package com.lachozag4.pisip.presentacion.dto.response;

import java.time.LocalDateTime;

public class PedidoResponseDTO {
	private int idpedido;
	private LocalDateTime fecha = LocalDateTime.now();
	private boolean estado;
	private String observaciones;
	
	public int getIdpedido() {
		return idpedido;
	}
	public void setIdpedido(int idpedido) {
		this.idpedido = idpedido;
	}
	public LocalDateTime getFecha() {
		return fecha;
	}
	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}
	public boolean isEstado() {
		return estado;
	}
	public void setEstado(boolean estado) {
		this.estado = estado;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	

}
