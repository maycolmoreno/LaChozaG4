package com.lachozag4.pisip.dominio.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 🔒 VALIDACIÓN GLOBAL
	@NotBlank(message = "El nombre de la categoría es obligatorio")
	@Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
	@Column(nullable = false, unique = true, length = 50)
	private String nombre;

	@Size(max = 150, message = "La descripción no debe superar 150 caracteres")
	@Column(length = 150)
	private String descripcion;

	@OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Producto> productos;
}
