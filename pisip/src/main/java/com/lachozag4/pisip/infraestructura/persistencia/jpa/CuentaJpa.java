package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.lachozag4.pisip.dominio.enums.EstadoCuenta;

@Entity
@Table(name = "cuenta")
@Getter
@Setter
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

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoCuenta estado;

	@Column(nullable = false)
	private double total;

	@ManyToOne
	@JoinColumn(name = "fkMesa")
	private MesaJpa fkMesa;

	@ManyToOne
	@JoinColumn(name = "fkCliente")
	private ClienteJpa fkCliente;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CuentaJpa that)) return false;
		return idcuenta != 0 && idcuenta == that.idcuenta;
	}

	@Override
	public int hashCode() { return getClass().hashCode(); }

	@Override
	public String toString() { return "CuentaJpa(id=" + idcuenta + ")"; }
}
