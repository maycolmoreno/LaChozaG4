package com.lachozag4.pisip.infraestructura.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.PedidoJpa;
import java.util.Optional;
import java.util.List;

@Repository
public interface IPedidoJpaRepositorio extends JpaRepository<PedidoJpa, Integer> {
    
    @Query("SELECT p FROM PedidoJpa p " +
           "LEFT JOIN FETCH p.FkUsuario " +
           "LEFT JOIN FETCH p.Fkmesa " +
           "LEFT JOIN FETCH p.Fkcliente " +
           "WHERE p.idpedido = :id")
    Optional<PedidoJpa> findByIdWithRelations(@Param("id") int id);
    
    @Query("SELECT DISTINCT p FROM PedidoJpa p " +
           "LEFT JOIN FETCH p.FkUsuario " +
           "LEFT JOIN FETCH p.Fkmesa " +
           "LEFT JOIN FETCH p.Fkcliente")
    List<PedidoJpa> findAllWithRelations();
}
