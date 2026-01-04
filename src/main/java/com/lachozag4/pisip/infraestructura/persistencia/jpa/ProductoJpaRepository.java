package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoJpaRepository extends JpaRepository<ProductoEntity, Long> {

    // Buscar productos por categoría (ej: Ceviches)
    List<ProductoEntity> findByCategoriaId(Long categoriaId);

    // Buscar productos activos con stock disponible
    List<ProductoEntity> findByActivoTrueAndStockActualGreaterThan(Integer stockMinimo);

    // Buscar productos por nombre (buscador)
    List<ProductoEntity> findByNombreContainingIgnoreCase(String nombre);
}

