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
@Table(name = "comedor")
@Getter
@Setter
@NoArgsConstructor
public class ComedorJpa implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idcomedor;

	@Column(nullable = false, unique = true, length = 100)
	private String nombre;

	@Column(length = 500)
	private String descripcion;

	@Column(nullable = false)
	private boolean estado;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ComedorJpa that)) return false;
		return idcomedor != 0 && idcomedor == that.idcomedor;
	}

	@Override
	public int hashCode() { return getClass().hashCode(); }

	@Override
	public String toString() { return "ComedorJpa(id=" + idcomedor + ")"; }
}
