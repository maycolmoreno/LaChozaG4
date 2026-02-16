package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cuenta")
@Data
@NoArgsConstructor
public class CuentaJpa implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idcuenta;

	@Column(nullable = false)
	private LocalDateTime fechaApertura;

	@Column
	private LocalDateTime fechaCierre;

	@Column(nullable = false, length = 20)
	private String estado; // ABIERTA, PAGADA, ANULADA

	@Column(nullable = false)
	private double total;

	@ManyToOne
	@JoinColumn(name = "fkMesa")
	private MesaJpa fkMesa;

	@ManyToOne
	@JoinColumn(name = "fkCliente")
	private ClienteJpa fkCliente;
}
