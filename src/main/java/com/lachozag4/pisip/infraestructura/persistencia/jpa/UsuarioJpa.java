package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "usuario")
@Data
public class UsuarioJpa implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idusuario;
	private String username;
	private String password;
	private String nombreCompleto;
	private String rol; // EJEMPLOS: 'ADMIN', 'CAMARERO', 'COCINA'
	private Boolean estado;

	/**
	 * // UsuarioJpa
	 * 
	 * @OneToMany(mappedBy = "FkUsuario") private List<PedidoJpa> pedidos;
	 */

}
