package com.lachozag4.pisip.dominio.entidades;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "configuracion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Configuracion {

    @Id
    private Long id = 1L; // Siempre usamos el ID 1 para asegurar que solo exista un registro de configuración

    @Column(nullable = false)
    private String nombreRestaurante;

    @Column(nullable = false)
    private String ruc;

    @Column(nullable = false)
    private Double porcentajeIva; // Este es el valor que el Admin podrá cambiar (ej. 15.0)

    private String direccion;
    
    private String telefono;

    private String mensajePieFactura; // Ej: "Gracias por su visita a La Choza"
}