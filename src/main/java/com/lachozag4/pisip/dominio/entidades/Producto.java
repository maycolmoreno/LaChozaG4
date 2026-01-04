package com.lachozag4.pisip.dominio.entidades;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// ❌ SIN anotaciones JPA (@Entity, @Table, @Column, @ManyToOne, etc.)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    
    private Long id;
    private String nombre;
    private Double precio;
    private Integer stockActual;
    private String descripcion;
    private Categoria categoria;  // Objeto completo, NO @ManyToOne
    private Boolean disponible;
    private String imagenUrl;  // ⭐ NUEVO CAMPO
    
    /* ===== Lógica de dominio ===== */
    
    public boolean tieneStockDisponible(int cantidadSolicitada) {
        return this.stockActual != null && this.stockActual >= cantidadSolicitada;
    }
    
    public void reducirStock(int cantidad) {
        if (tieneStockDisponible(cantidad)) {
            this.stockActual -= cantidad;
        }
    }
    
    public String getImagenUrlODefecto() {
        return (imagenUrl != null && !imagenUrl.isBlank()) 
            ? imagenUrl 
            : "/images/default-product.jpg";
    }
}