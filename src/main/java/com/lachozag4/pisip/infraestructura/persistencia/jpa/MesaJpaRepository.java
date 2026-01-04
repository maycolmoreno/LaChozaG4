package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.MesaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface MesaJpaRepository extends JpaRepository<MesaEntity, Long> { 
    // Mesas por estado: LIBRE, OCUPADA, MANTENIMIENTO
    List<MesaEntity> findByEstado(String estado);

    // Buscar mesa por número físico
    Optional<MesaEntity> findByNumero(Integer numero);

    // Mesas con capacidad suficiente
    List<MesaEntity> findByCapacidadGreaterThanEqual(Integer personas);
}
