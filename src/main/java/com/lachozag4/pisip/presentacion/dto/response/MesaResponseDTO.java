package com.lachozag4.pisip.presentacion.dto.response;

public class MesaResponseDTO {
	private int idmesa;
	private Integer numero;
	private Integer capacidad;
	private boolean estado; // "LIBRE", "OCUPADA"
	private String ubicacion; // "Salon 1", "Salon 2"

	public int getIdmesa() {
		return idmesa;
	}

	public void setIdmesa(int idmesa) {
		this.idmesa = idmesa;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Integer getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(Integer capacidad) {
		this.capacidad = capacidad;
	}

	public boolean isEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		this.estado = estado;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

}
