package com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;

    @Column(length = 255)
    private String descripcion;

    // Si tu versión de Spring/Java no reconoce las anotaciones de Lombok (@Getter/@Setter)
    // recuerda generar los Getters y Setters manualmente.
}