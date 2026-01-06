package com.lachozag4.pisip.aplicacion.puertos.salida;

import com.lachozag4.pisip.dominio.entidades.Pedido;
import java.util.List;
import java.util.Optional;

public interface PedidoRepositoryPort {
    
    Pedido guardar(Pedido pedido);
    
    Optional<Pedido> buscarPorId(Long id);
    
    List<Pedido> listarTodos();
    
    List<Pedido> buscarPorMesaId(Long mesaId);
    
    List<Pedido> buscarPorEstado(String estado);
    
    void eliminar(Long id);
    
    boolean existePorId(Long id);
}