package com.lachozag4.pisip.dominio.entidades;

import java.io.Serializable;

public class Cliente implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int idcliente;
	private final String identificacion; // Cédula o RUC
	private final String nombre;
	private final String direccion;
	private final String telefono;
	private final String email;
	public Cliente(int idcliente, String identificacion, String nombre, String direccion, String telefono,
			String email) {
		
		this.idcliente = idcliente;
		this.identificacion = identificacion;
		this.nombre = nombre;
		this.direccion = direccion;
		this.telefono = telefono;
		this.email = email;
	}
	public int getIdcliente() {
		return idcliente;
	}
	public String getIdentificacion() {
		return identificacion;
	}
	public String getNombre() {
		return nombre;
	}
	public String getDireccion() {
		return direccion;
	}
	public String getTelefono() {
		return telefono;
	}
	public String getEmail() {
		return email;
	}

	
}