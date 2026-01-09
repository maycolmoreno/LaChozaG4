package com.lachozag4.pisip.dominio.entidades;

import java.io.Serializable;

public class Mesa implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int idmesa;
	private final Integer numero;
	private final Integer capacidad;
	private final boolean estado; // "LIBRE", "OCUPADA"
	private final String ubicacion; // "Salon 1", "Salon 2"
	public Mesa(int idmesa, Integer numero, Integer capacidad, boolean estado, String ubicacion) {
		
		this.idmesa = idmesa;
		this.numero = numero;
		this.capacidad = capacidad;
		this.estado = estado;
		this.ubicacion = ubicacion;
	}
	public int getIdmesa() {
		return idmesa;
	}
	public Integer getNumero() {
		return numero;
	}
	public Integer getCapacidad() {
		return capacidad;
	}
	public boolean isEstado() {
		return estado;
	}
	public String getUbicacion() {
		return ubicacion;
	}
	
	
	
	
}