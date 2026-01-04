package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import com.lachozag4.pisip.aplicacion.puertos.salida.PedidoRepositoryPort;
import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.PedidoJpaRepository;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.PedidoEntity;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.PedidoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PedidoPersistenceAdapter implements PedidoRepositoryPort {
    
    private final PedidoJpaRepository pedidoJpaRepository;
    private final PedidoMapper pedidoMapper;
    
    @Override
    public Pedido guardar(Pedido pedido) {
        PedidoEntity entity = pedidoMapper.aEntity(pedido);
        PedidoEntity guardado = pedidoJpaRepository.save(entity);
        return pedidoMapper.aDominio(guardado);
    }
    
    @Override
    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoJpaRepository.findById(id)
                .map(pedidoMapper::aDominio);
    }
    
    @Override
    public List<Pedido> listarTodos() {
        return pedidoJpaRepository.findAll()
                .stream()
                .map(pedidoMapper::aDominio)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Pedido> buscarPorMesaId(Long mesaId) {
        return pedidoJpaRepository.findByMesaId(mesaId)
                .stream()
                .map(pedidoMapper::aDominio)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Pedido> buscarPorEstado(String estado) {
        return pedidoJpaRepository.findByEstado(estado)
                .stream()
                .map(pedidoMapper::aDominio)
                .collect(Collectors.toList());
    }
    
    @Override
    public void eliminar(Long id) {
        pedidoJpaRepository.deleteById(id);
    }
    
    @Override
    public boolean existePorId(Long id) {
        return pedidoJpaRepository.existsById(id);
    }
}