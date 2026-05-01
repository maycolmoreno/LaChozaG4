package com.lachozag4.pisip.dominio.entidades;

import java.io.Serializable;

public class Comedor implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int idcomedor;
	private final String nombre;
	private final String descripcion;
	private final boolean estado;

	public Comedor(int idcomedor, String nombre, String descripcion, boolean estado) {
		this.idcomedor = idcomedor;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.estado = estado;
	}

	public int getIdcomedor() {
		return idcomedor;
	}

	public String getNombre() {
		return nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public boolean getEstado() {
		return estado;
	}

	@Override
	public String toString() {
		return "Comedor{" +
				"idcomedor=" + idcomedor +
				", nombre='" + nombre + '\'' +
				", descripcion='" + descripcion + '\'' +
				", estado=" + estado +
				'}';
	}

}
