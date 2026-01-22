package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "cliente")
@Data
public class ClienteJpa implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idcliente;
	private String identificacion; // Cédula o RUC
	private String nombre;
	private String direccion;
	private String telefono;
	private String email;

	/**
	 * 
	 * 
	 * @OneToMany(mappedBy = "Fkcliente") private List<PedidoJpa> pedidos;
	 */

}
