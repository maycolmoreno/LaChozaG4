package com.lachozag4.pisip.dominio.entidades;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDetalle {

    private Long id;
    private Producto producto; // Relación con el objeto de dominio Producto
    private Integer cantidad;
    private Double precioUnitario; // Importante: Guardamos el precio del momento

    /**
     * Lógica de negocio: Calcula el subtotal de esta línea.
     * Útil para mostrar en el carrito o en la factura antes de impuestos.
     */
    public Double calcularSubtotal() {
        if (this.precioUnitario == null || this.cantidad == null) {
            return 0.0;
        }
        return this.precioUnitario * this.cantidad;
    }
}