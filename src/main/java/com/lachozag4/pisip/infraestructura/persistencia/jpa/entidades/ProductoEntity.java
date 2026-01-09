package com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(length = 500)
    private String descripcion;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;
    
    @Column(nullable = false)
    private Boolean activo;
    
    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id")
    private CategoriaEntity categoria;
    
    @PrePersist
    protected void onCreate() {
        if (activo == null) {
            activo = true;
        }
        if (stockActual == null) {
            stockActual = 0;
        }
    }
}