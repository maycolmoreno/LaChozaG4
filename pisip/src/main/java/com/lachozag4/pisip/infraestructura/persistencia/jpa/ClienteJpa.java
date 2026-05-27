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
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor
public class ClienteJpa implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idcliente;
	
	@Column(nullable = false, length = 150)
	private String nombre;

	@Column(nullable = false, unique = true, length = 20)
	private String cedula;

	@Column(length = 20)
	private String telefono;

	@Column(length = 255)
	private String direccion;

	@Column(unique = true, length = 150)
	private String email;

	@Column(nullable = false)
	private boolean estado;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ClienteJpa that)) return false;
		return idcliente != 0 && idcliente == that.idcliente;
	}

	@Override
	public int hashCode() { return getClass().hashCode(); }

	@Override
	public String toString() { return "ClienteJpa(id=" + idcliente + ")"; }
}
