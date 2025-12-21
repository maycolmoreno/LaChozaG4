package com.lachozag4.pisip.dominio.entidades;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    private Long id;
    private String identificacion; // Cédula o RUC
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;

    /**
     * Ejemplo de lógica de negocio: Validar si la identificación es válida
     * (Específica para Ecuador si es el caso)
     */
    public boolean esIdentificacionValida() {
        return this.identificacion != null && this.identificacion.length() >= 10;
    }
}