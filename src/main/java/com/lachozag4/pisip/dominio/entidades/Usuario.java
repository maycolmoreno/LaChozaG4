
package com.lachozag4.pisip.dominio.entidades;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private String username;
    private String password;
    
    // Aquí defines: "ADMIN", "CAMARERO", "COCINA"
    private String rol; 
}