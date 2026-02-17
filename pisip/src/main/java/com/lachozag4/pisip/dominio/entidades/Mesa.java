package com.lachozag4.pisip.dominio.entidades;

import java.io.Serializable;

public class Mesa implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int idmesa;
	private final int numero;
	private final int capacidad;
	private final boolean estado;

	public Mesa(int idmesa, int numero, int capacidad, boolean estado) {

		this.idmesa = idmesa;
		this.numero = numero;
		this.capacidad = capacidad;
		this.estado = estado;
	}

	public int getIdmesa() {
		return idmesa;
	}

	public int getNumero() {
		return numero;
	}

	public int getCapacidad() {
		return capacidad;
	}

	public boolean getEstado() {
		return estado;
	}

	@Override
	public String toString() {
		return "Mesa{" +
				"idmesa=" + idmesa +
				", numero=" + numero +
				", capacidad=" + capacidad +
				", estado=" + estado +
				'}';
	}

}
