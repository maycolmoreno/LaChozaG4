package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import com.lachozag4.pisip.dominio.entidades.Configuracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracionRepository extends JpaRepository<Configuracion, Long> {
    // No necesitas métodos adicionales, JpaRepository ya te da 
    // findById(1L) para obtener el IVA y save() para actualizarlo.
}