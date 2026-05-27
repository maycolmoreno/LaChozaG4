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
@Table(name = "categoria")
@Getter
@Setter
@NoArgsConstructor
public class CategoriaJpa implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idcategoria;

	@Column(nullable = false, unique = true, length = 100)
	private String nombre;

	@Column(length = 500)
	private String descripcion;

	@Column(nullable = false)
	private boolean estado;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CategoriaJpa that)) return false;
		return idcategoria != 0 && idcategoria == that.idcategoria;
	}

	@Override
	public int hashCode() { return getClass().hashCode(); }

	@Override
	public String toString() { return "CategoriaJpa(id=" + idcategoria + ")"; }
}
