package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoJpaRepository extends JpaRepository<PedidoEntity, Long> {

    // Buscar pedidos por estado (ej: "PENDIENTE", "PARA_COCINA")
    List<PedidoEntity> findByEstado(String estado);

    // Buscar pedidos activos de una mesa (no pagados)
    @Query("""
        SELECT p 
        FROM PedidoEntity p 
        WHERE p.mesa.id = :mesaId 
          AND p.estado <> 'PAGADO'
    """)
    List<PedidoEntity> findPedidosActivosPorMesa(Long mesaId);

    // Buscar pedidos por identificación del cliente
    List<PedidoEntity> findByClienteIdentificacion(String identificacion);
}
