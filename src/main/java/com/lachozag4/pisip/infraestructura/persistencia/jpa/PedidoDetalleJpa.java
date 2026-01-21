package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "pedidodetalle")
@Data
public class PedidoDetalleJpa implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idpedidodetalle;
	private Integer cantidad; // >= 1

	// PedidoJpa
	@ManyToOne
	@JoinColumn(name = "Fkproducto")
	private ProductoJpa Fkproducto;
	// PedidoDetalleJpa
	@ManyToOne
	@JoinColumn(name = "Fkpedidodet")
	private PedidoJpa Fkpedidodet;

}
