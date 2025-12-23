package com.lachozag4.pisip.dominio.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios") // Se usa "usuarios" porque "user" es palabra reservada en PostgreSQL
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String nombreCompleto;

	@Column(nullable = false)
	private String rol; // EJEMPLOS: 'ADMIN', 'CAMARERO', 'COCINA'

	private Boolean activo = true;

	/**
	 * Lógica de Dominio: Verifica si el usuario tiene permisos de administrador
	 */
	public boolean esAdministrador() {
		return "ADMIN".equalsIgnoreCase(this.rol);
	}
}