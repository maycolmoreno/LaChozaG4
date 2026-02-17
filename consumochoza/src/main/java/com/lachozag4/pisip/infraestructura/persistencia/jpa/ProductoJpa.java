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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "producto", uniqueConstraints = { @UniqueConstraint(columnNames = { "nombre", "idcategoria" }) })
@Data
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

}
