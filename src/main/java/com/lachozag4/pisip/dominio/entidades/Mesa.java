package com.lachozag4.pisip.dominio.entidades;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mesa {

    private Long id;
    private Integer numero;
    private Integer capacidad;
    private String estado; // "LIBRE", "OCUPADA", "MANTENIMIENTO"
    private String ubicacion; // "Terraza", "Planta Baja", "VIP"

    /**
     * Lógica de negocio: Verifica si la mesa puede recibir comensales.
     */
    public boolean estaDisponible() {
        return "LIBRE".equalsIgnoreCase(this.estado);
    }

    /**
     * Lógica de negocio: Cambia el estado al asignar un pedido.
     */
    public void ocupar() {
        if (estaDisponible()) {
            this.estado = "OCUPADA";
        }
    }

    /**
     * Lógica de negocio: Libera la mesa tras el pago.
     */
    public void liberar() {
        this.estado = "LIBRE";
    }
}