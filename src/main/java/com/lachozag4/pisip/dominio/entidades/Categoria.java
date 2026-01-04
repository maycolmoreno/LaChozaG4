package com.lachozag4.pisip.dominio.entidades;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

// ❌ SIN anotaciones JPA (@Entity, @Table, @OneToMany, @JsonIgnore, etc.)

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private List<Producto> productos;  // Simple List, NO @OneToMany
    
    // Lógica de negocio (opcional)
    public boolean esNombreValido() {
        return nombre != null && nombre.length() >= 3 && nombre.length() <= 50;
    }
}