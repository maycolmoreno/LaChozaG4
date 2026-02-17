package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
public class ClienteJpa implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idcliente;
	
	@Column(nullable = false, length = 150)
	private String nombre;

	@Column(nullable = false, unique = true, length = 20)
	private String cedula;

	@Column(length = 20)
	private String telefono;

	@Column(length = 255)
	private String direccion;

	@Column(unique = true, length = 150)
	private String email;

	@Column(nullable = false)
	private boolean estado;

}
