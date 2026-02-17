package com.lachozag4.pisip.dominio.entidades;

import java.io.Serializable;

public class Usuario implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int idusuario;
	private final String username;
	private final String password;
	private final String nombreCompleto;
	private final String rol;
	private final boolean estado;
	private final boolean requiereCambioPassword;

	public Usuario(int idusuario, String username, String password, String nombreCompleto, String rol, boolean estado,
			boolean requiereCambioPassword) {
		this.idusuario = idusuario;
		this.username = username;
		this.password = password;
		this.nombreCompleto = nombreCompleto;
		this.rol = rol;
		this.estado = estado;
		this.requiereCambioPassword = requiereCambioPassword;
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

	public boolean getEstado() {
		return estado;
	}

	public boolean isRequiereCambioPassword() {
		return requiereCambioPassword;
	}

	@Override
	public String toString() {
		return "Usuario{" +
				"idusuario=" + idusuario +
				", username='" + username + '\'' +
				", nombreCompleto='" + nombreCompleto + '\'' +
				", rol='" + rol + '\'' +
				", estado=" + estado +
				'}';
	}
}
