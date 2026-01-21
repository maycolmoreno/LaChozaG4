package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "pedido")
@Data
public class PedidoJpa implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idpedido;
	private LocalDateTime fecha;
	private boolean estado;
	private String observaciones;

	// UsuarioJpa
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FkUsuario")
	private UsuarioJpa FkUsuario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Fkmesa")
	private MesaJpa Fkmesa;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Fkcliente")
	private ClienteJpa Fkcliente;
	
	@OneToMany(mappedBy = "Fkpedidodet", cascade = CascadeType.ALL)
	private List<PedidoDetalleJpa> pedidos;

}
