package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.ProductoEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductoMapper {

    /**
     * Convierte de Entity a Dominio
     */
    public static Producto aDominio(ProductoEntity entity) {
        if (entity == null) {
            return null;
        }

        Producto dominio = new Producto();
        dominio.setId(entity.getId());
        dominio.setNombre(entity.getNombre());
        dominio.setDescripcion(entity.getDescripcion());
        
        // ⭐ Convertir BigDecimal a Double
        dominio.setPrecio(entity.getPrecio() != null ? entity.getPrecio().doubleValue() : null);
        
        dominio.setStockActual(entity.getStockActual());
        dominio.setDisponible(entity.getActivo());
        dominio.setImagenUrl(entity.getImagenUrl());

        // Mapear categoría
        if (entity.getCategoria() != null) {
            dominio.setCategoria(CategoriaMapper.aDominio(entity.getCategoria()));
        }

        return dominio;
    }

    /**
     * Convierte de Dominio a Entity
     */
    public static ProductoEntity aEntity(Producto dominio) {
        if (dominio == null) {
            return null;
        }

        ProductoEntity entity = new ProductoEntity();
        entity.setId(dominio.getId());
        entity.setNombre(dominio.getNombre());
        entity.setDescripcion(dominio.getDescripcion());
        
        // ⭐ Convertir Double a BigDecimal
        entity.setPrecio(dominio.getPrecio() != null 
            ? BigDecimal.valueOf(dominio.getPrecio()) 
            : null);
        
        entity.setStockActual(dominio.getStockActual());
        entity.setImagenUrl(dominio.getImagenUrl());

        // Mapear disponible → activo
        if (dominio.getDisponible() != null) {
            entity.setActivo(dominio.getDisponible());
        } else {
            entity.setActivo(true); // Por defecto activo
        }

        // Mapear categoría
        if (dominio.getCategoria() != null) {
            entity.setCategoria(CategoriaMapper.aEntity(dominio.getCategoria()));
        }

        return entity;
    }

    /**
     * Convierte lista de Entity a lista de Dominio
     */
    public static List<Producto> aDominioList(List<ProductoEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(ProductoMapper::aDominio)
                .collect(Collectors.toList());
    }

    /**
     * Convierte lista de Dominio a lista de Entity
     */
    public static List<ProductoEntity> aEntityList(List<Producto> productos) {
        if (productos == null || productos.isEmpty()) {
            return Collections.emptyList();
        }
        return productos.stream()
                .map(ProductoMapper::aEntity)
                .collect(Collectors.toList());
    }
}