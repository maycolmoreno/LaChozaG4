package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import com.lachozag4.pisip.aplicacion.puertos.salida.ProductoRepositoryPort;
import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.ProductoJpaRepository;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.ProductoEntity;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.ProductoMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductoPersistenceAdapter implements ProductoRepositoryPort {

    private final ProductoJpaRepository productoJpaRepository;

    public ProductoPersistenceAdapter(ProductoJpaRepository productoJpaRepository) {
        this.productoJpaRepository = productoJpaRepository;
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        return productoJpaRepository.findById(id)
                .map(ProductoMapper::aDominio); // ✅ FORMA CORRECTA
    }

    @Override
    public List<Producto> listarTodos() {
        return productoJpaRepository.findAll()
                .stream()
                .map(ProductoMapper::aDominio) // ✅ CORRECTO
                .toList();
    }

    @Override
    public Producto guardar(Producto producto) {
        ProductoEntity entity = ProductoMapper.aEntity(producto);
        ProductoEntity guardado = productoJpaRepository.save(entity);
        return ProductoMapper.aDominio(guardado);
    }
}
