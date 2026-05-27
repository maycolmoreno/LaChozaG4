package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
public class UsuarioJpa implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idusuario;

	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@Column(nullable = false, length = 255)
	private String password;

	@Column(nullable = false, length = 150)
	private String nombreCompleto;

	@Column(nullable = false, length = 50)
	private String rol;

	@Column(nullable = false)
	private boolean estado;

	@Column(nullable = false)
	private boolean requiereCambioPassword;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UsuarioJpa that)) return false;
		return idusuario != 0 && idusuario == that.idusuario;
	}

	@Override
	public int hashCode() { return getClass().hashCode(); }

	@Override
	public String toString() { return "UsuarioJpa(id=" + idusuario + ")"; }
}
