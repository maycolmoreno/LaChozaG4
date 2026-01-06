package com.lachozag4.pisip.aplicacion.puertos.salida;

import com.lachozag4.pisip.dominio.entidades.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoRepositoryPort {
    
    Producto guardar(Producto producto);
    
    Optional<Producto> buscarPorId(Long id);
    
    List<Producto> listarTodos();
    
    void eliminar(Long id);
    
    boolean existePorId(Long id);
    
    // ⭐ Métodos que estaban faltando
    List<Producto> listarActivos();
    
    List<Producto> buscarPorNombre(String nombre);
    
    // Métodos adicionales opcionales
    List<Producto> listarPorCategoria(Long categoriaId);
}