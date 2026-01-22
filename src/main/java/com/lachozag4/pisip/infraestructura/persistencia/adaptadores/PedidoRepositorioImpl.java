package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;
import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.dominio.repositorios.IPedidoRepositorio;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.PedidoJpa;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IPedidoJpaMapper;
import com.lachozag4.pisip.infraestructura.repositorios.IPedidoJpaRepositorio;

public class PedidoRepositorioImpl implements IPedidoRepositorio {
    
    private final IPedidoJpaRepositorio jparepository;
    private final IPedidoJpaMapper entityMapper;
    
    public PedidoRepositorioImpl(IPedidoJpaRepositorio jparepository, IPedidoJpaMapper entityMapper) {
        this.jparepository = jparepository;
        this.entityMapper = entityMapper;
    }
    
    @Override
    public Pedido guardar(Pedido pedido) {
        PedidoJpa entity = entityMapper.toEntity(pedido);
        PedidoJpa guardado = jparepository.save(entity);
        // Recargar con relaciones
        return jparepository.findByIdWithRelations(guardado.getIdpedido())
            .map(entityMapper::toDomain)
            .orElse(entityMapper.toDomain(guardado));
    }
    
    @Override
    public Optional<Pedido> BuscarPorId(int id) {
        // USAR findByIdWithRelations NO findById
        return jparepository.findByIdWithRelations(id).map(entityMapper::toDomain);
    }
    
    @Override
    public List<Pedido> listarTodos() {
        // USAR findAllWithRelations NO findAll
        return jparepository.findAllWithRelations().stream().map(entityMapper::toDomain).toList();
    }
    
    @Override
    public void eliminar(int id) {
        jparepository.deleteById(id);
    }
}