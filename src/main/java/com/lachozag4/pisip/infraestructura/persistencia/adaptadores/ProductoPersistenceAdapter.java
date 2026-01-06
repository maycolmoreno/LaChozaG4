package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import com.lachozag4.pisip.aplicacion.puertos.salida.ProductoRepositoryPort;
import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.ProductoJpaRepository;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.ProductoEntity;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.ProductoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductoPersistenceAdapter implements ProductoRepositoryPort {
    
    private final ProductoJpaRepository productoJpaRepository;
    
    @Override
    public Producto guardar(Producto producto) {
        ProductoEntity entity = ProductoMapper.aEntity(producto);
        ProductoEntity guardado = productoJpaRepository.save(entity);
        return ProductoMapper.aDominio(guardado);
    }
    
    @Override
    public Optional<Producto> buscarPorId(Long id) {
        return productoJpaRepository.findById(id)
                .map(ProductoMapper::aDominio);
    }
    
    @Override
    public List<Producto> listarTodos() {
        return productoJpaRepository.findAll().stream()
                .map(ProductoMapper::aDominio)
                .collect(Collectors.toList());
    }
    
    @Override
    public void eliminar(Long id) {
        productoJpaRepository.deleteById(id);
    }
    
    @Override
    public boolean existePorId(Long id) {
        return productoJpaRepository.existsById(id);
    }
    
    @Override
    public List<Producto> listarActivos() {
        return productoJpaRepository.findByActivoTrue().stream()
                .map(ProductoMapper::aDominio)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Producto> buscarPorNombre(String nombre) {
        return productoJpaRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(ProductoMapper::aDominio)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Producto> listarPorCategoria(Long categoriaId) {
        return productoJpaRepository.findByCategoriaId(categoriaId).stream()
                .map(ProductoMapper::aDominio)
                .collect(Collectors.toList());
    }
}