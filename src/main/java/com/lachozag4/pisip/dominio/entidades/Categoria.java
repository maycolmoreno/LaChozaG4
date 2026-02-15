package com.lachozag4.pisip.dominio.entidades;

import java.io.Serializable;

public class Categoria implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int idcategoria;
	private final String nombre;
	private final String descripcion;
	private final boolean estado;

	public Categoria(int idcategoria, String nombre, String descripcion, boolean estado) {

		this.idcategoria = idcategoria;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.estado = estado;
	}

	public int getIdcategoria() {
		return idcategoria;
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
		return "Categoria{" +
				"idcategoria=" + idcategoria +
				", nombre='" + nombre + '\'' +
				", descripcion='" + descripcion + '\'' +
				", estado=" + estado +
				'}';
	}

}
