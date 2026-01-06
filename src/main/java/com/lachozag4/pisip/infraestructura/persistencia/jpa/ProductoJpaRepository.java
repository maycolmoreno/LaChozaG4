package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoJpaRepository extends JpaRepository<ProductoEntity, Long> {
    
    List<ProductoEntity> findByActivoTrue();
    
    List<ProductoEntity> findByCategoriaId(Long categoriaId);
    
    List<ProductoEntity> findByNombreContainingIgnoreCase(String nombre);
}