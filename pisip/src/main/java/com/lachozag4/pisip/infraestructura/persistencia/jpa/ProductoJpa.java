package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "producto", uniqueConstraints = { @UniqueConstraint(columnNames = { "nombre", "idcategoria" }) })
@Getter
@Setter
@NoArgsConstructor
public class ProductoJpa implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idproducto;

	@Column(nullable = false, length = 200)
	private String nombre;

	@Column(nullable = false)
	private double precio;

	@Column(nullable = false)
	private int stockActual;

	@Column(length = 500)
	private String descripcion;

	@Column(name = "imagen_url", length = 500)
	private String imagenUrl;

	@Column(nullable = false)
	private boolean estado;

	@ManyToOne
	@JoinColumn(name = "fkCategoriaId", nullable = false)
	private CategoriaJpa fkCategoriaId;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ProductoJpa that)) return false;
		return idproducto != 0 && idproducto == that.idproducto;
	}

	@Override
	public int hashCode() { return getClass().hashCode(); }

	@Override
	public String toString() { return "ProductoJpa(id=" + idproducto + ")"; }
}
