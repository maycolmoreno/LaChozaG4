package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoJpaRepository extends JpaRepository<PedidoEntity, Long> {
    
    // Buscar pedidos por mesa
    List<PedidoEntity> findByMesaId(Long mesaId);
    
    // Buscar pedidos por estado
    List<PedidoEntity> findByEstado(String estado);
    
    // Buscar pedidos por cliente (opcional)
    List<PedidoEntity> findByClienteId(Long clienteId);
}