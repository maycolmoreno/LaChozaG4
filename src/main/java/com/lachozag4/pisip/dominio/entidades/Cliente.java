package com.lachozag4.pisip.dominio.entidades;

import java.io.Serializable;

public class Cliente implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int idcliente;
	private final String nombre;
	private final String cedula;
	private final String telefono;
	private final String direccion;
	private final String email;
	private final boolean estado;

	public Cliente(int idcliente, String nombre, String cedula, String telefono, String direccion, String email, boolean estado) {
		this.idcliente = idcliente;
		this.nombre = nombre;
		this.cedula = cedula;
		this.telefono = telefono;
		this.direccion = direccion;
		this.email = email;
		this.estado = estado;
	}

	public int getIdcliente() {
		return idcliente;
	}

	public String getNombre() {
		return nombre;
	}

	public String getCedula() {
		return cedula;
	}

	public String getTelefono() {
		return telefono;
	}

	public String getDireccion() {
		return direccion;
	}

	public String getEmail() {
		return email;
	}

	public boolean getEstado() {
		return estado;
	}

	@Override
	public String toString() {
		return "Cliente{" +
				"idcliente=" + idcliente +
				", nombre='" + nombre + '\'' +
				", cedula='" + cedula + '\'' +
				", telefono='" + telefono + '\'' +
				", direccion='" + direccion + '\'' +
				", email='" + email + '\'' +
				", estado=" + estado +
				'}';
	}

}
