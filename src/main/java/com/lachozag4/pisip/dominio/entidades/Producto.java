package com.lachozag4.pisip.dominio.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private Double precio;

    @Column(name = "stock_actual")
    private Integer stockActual;

    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    private Boolean disponible;

    /* ===== Lógica de dominio ===== */

    public boolean tieneStockDisponible(int cantidadSolicitada) {
        return this.stockActual != null && this.stockActual >= cantidadSolicitada;
    }

    public void reducirStock(int cantidad) {
        if (tieneStockDisponible(cantidad)) {
            this.stockActual -= cantidad;
        }
    }
}
