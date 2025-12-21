package com.lachozag4.pisip.dominio.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre; // Ej: "Ceviches", "Bebidas", "Postres"

    private String descripcion; // Ej: "Especialidades de la casa con mariscos frescos"

    // Relación uno a muchos: Una categoría tiene muchos productos
    // mappedBy indica que la relación la gestiona el campo 'categoria' en la clase Producto
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL)
    private List<Producto> productos;
}