package com.lachozag4.pisip.dominio.entidades;

import java.io.Serializable;

public class Mesa implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int idmesa;
	private final int numero;
	private final int capacidad;
	private final boolean estado;
	private final Integer idcomedor;

	public Mesa(int idmesa, int numero, int capacidad, boolean estado, Integer idcomedor) {
		this.idmesa = idmesa;
		this.numero = numero;
		this.capacidad = capacidad;
		this.estado = estado;
		this.idcomedor = idcomedor;
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

	public Integer getIdcomedor() {
		return idcomedor;
	}

	/** Crea una copia de esta mesa con el estado indicado. */
	public Mesa conEstado(boolean nuevoEstado) {
		return new Mesa(this.idmesa, this.numero, this.capacidad, nuevoEstado, this.idcomedor);
	}

	@Override
	public String toString() {
		return "Mesa{" +
				"idmesa=" + idmesa +
				", numero=" + numero +
				", capacidad=" + capacidad +
				", estado=" + estado +
				", idcomedor=" + idcomedor +
				'}';
	}

}
