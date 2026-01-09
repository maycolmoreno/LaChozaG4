package com.lachozag4.pisip.dominio.entidades;

import java.io.Serializable;



public class Usuario implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int idusuario;	
	private final String username;	
	private final String password;	
	private final String nombreCompleto;	
	private final String rol; // EJEMPLOS: 'ADMIN', 'CAMARERO', 'COCINA'
	private final Boolean activo = true;
	public Usuario(int idusuario, String username, String password, String nombreCompleto, String rol) {
		this.idusuario = idusuario;
		this.username = username;
		this.password = password;
		this.nombreCompleto = nombreCompleto;
		this.rol = rol;
	}
	public int getIdusuario() {
		return idusuario;
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public String getNombreCompleto() {
		return nombreCompleto;
	}
	public String getRol() {
		return rol;
	}
	public Boolean getActivo() {
		return activo;
	}
	
	
}