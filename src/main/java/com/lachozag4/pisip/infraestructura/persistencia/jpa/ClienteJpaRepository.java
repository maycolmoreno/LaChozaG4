package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, Long> {

    // Buscar cliente por identificación (Cédula/RUC)
    Optional<ClienteEntity> findByIdentificacion(String identificacion);

    // Buscar cliente por email
    Optional<ClienteEntity> findByEmail(String email);

    // Verificar si existe cliente por identificación
    boolean existsByIdentificacion(String identificacion);
}
