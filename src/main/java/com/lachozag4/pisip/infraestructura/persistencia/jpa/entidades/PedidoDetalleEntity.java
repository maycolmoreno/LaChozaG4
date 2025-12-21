package com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pedido_detalles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDetalleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoEntity pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private ProductoEntity producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Double precioUnitario; // Precio al momento de la venta

    @Column(nullable = false)
    private Double subtotal;

    @PrePersist
    @PreUpdate
    private void calcularSubtotal() {
        this.subtotal = this.cantidad * this.precioUnitario;
    }
}