package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.time.LocalDateTime;

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
@Table(name = "caja_turno")
@Getter
@Setter
@NoArgsConstructor
public class CajaTurnoJpa implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idcaja;

	@Column(nullable = false)
	private LocalDateTime fechaApertura;

	@Column
	private LocalDateTime fechaCierre;

	@Column(nullable = false)
	private double montoInicial;

	@Column
	private Double montoEsperadoCierre;

	@Column
	private Double montoDeclaradoCierre;

	@Column
	private Double diferencia;

	@Column(nullable = false, length = 20)
	private String estado;

	@Column(nullable = false, length = 100)
	private String usuarioApertura;

	@Column(length = 100)
	private String usuarioCierre;

	@Column(length = 255)
	private String observaciones;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CajaTurnoJpa that)) return false;
		return idcaja != 0 && idcaja == that.idcaja;
	}

	@Override
	public int hashCode() { return getClass().hashCode(); }

	@Override
	public String toString() { return "CajaTurnoJpa(id=" + idcaja + ")"; }
}
