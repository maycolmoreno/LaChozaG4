package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "mesa")
@Getter
@Setter
@NoArgsConstructor
public class MesaJpa implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idmesa;

	@Column(nullable = false, unique = true)
	private int numero;

	@Column(nullable = false)
	private int capacidad;

	@Column(nullable = false)
	private boolean estado;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "idcomedor")
	private ComedorJpa fkComedor;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MesaJpa that)) return false;
		return idmesa != 0 && idmesa == that.idmesa;
	}

	@Override
	public int hashCode() { return getClass().hashCode(); }

	@Override
	public String toString() { return "MesaJpa(id=" + idmesa + ")"; }
}
