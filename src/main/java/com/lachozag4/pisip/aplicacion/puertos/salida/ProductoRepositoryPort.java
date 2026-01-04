package com.lachozag4.pisip.aplicacion.puertos.salida;

import com.lachozag4.pisip.dominio.entidades.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoRepositoryPort {
    
    Optional<Producto> buscarPorId(Long id);
    List<Producto> listarTodos();
    List<Producto> listarActivos();
    Producto guardar(Producto producto);
    void eliminar(Long id);
    boolean existePorId(Long id);
    List<Producto> buscarPorCategoriaId(Long categoriaId);
    List<Producto> buscarPorNombre(String nombre);
}