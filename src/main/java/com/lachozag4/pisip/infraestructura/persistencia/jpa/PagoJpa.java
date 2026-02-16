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
@Table(name = "pago")
@Data
@NoArgsConstructor
public class PagoJpa implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idpago;

	@Column(nullable = false)
	private LocalDateTime fecha;

	@Column(nullable = false)
	private double monto;

	@Column(nullable = false, length = 30)
	private String metodo;

	@Column(length = 120)
	private String referencia;

	@Column(nullable = false, length = 100)
	private String usuario;

	@ManyToOne
	@JoinColumn(name = "fkCuenta", nullable = false)
	private CuentaJpa fkCuenta;

	@ManyToOne
	@JoinColumn(name = "fkCajaTurno", nullable = false)
	private CajaTurnoJpa fkCajaTurno;
}
