package com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 15)
	private String identificacion; // Cédula o RUC

	@Column(nullable = false, length = 100)
	private String nombre;

	@Column(length = 150)
	private String direccion;

	@Column(length = 20)
	private String telefono;

	@Column(length = 100)
	private String email;
}