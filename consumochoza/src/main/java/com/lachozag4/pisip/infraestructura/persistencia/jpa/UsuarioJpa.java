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
@Table(name = "usuario")
@Data
@NoArgsConstructor
public class UsuarioJpa implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idusuario;

	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@Column(nullable = false, length = 255)
	private String password;

	@Column(nullable = false, length = 150)
	private String nombreCompleto;

	@Column(nullable = false, length = 50)
	private String rol;

	@Column(nullable = false)
	private boolean estado;

	@Column(nullable = false)
	private boolean requiereCambioPassword;

}
