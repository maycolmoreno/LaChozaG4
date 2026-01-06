package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import com.lachozag4.pisip.dominio.entidades.Categoria;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.CategoriaEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoriaMapper {
    
    /**
     * Convierte de Entity a Dominio
     */
    public static Categoria aDominio(CategoriaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Categoria dominio = new Categoria();
        dominio.setId(entity.getId());
        dominio.setNombre(entity.getNombre());
        dominio.setDescripcion(entity.getDescripcion());
        // No mapeamos productos para evitar lazy loading
        
        return dominio;
    }
    
    /**
     * Convierte de Dominio a Entity
     */
    public static CategoriaEntity aEntity(Categoria dominio) {
        if (dominio == null) {
            return null;
        }
        
        CategoriaEntity entity = new CategoriaEntity();
        entity.setId(dominio.getId());
        entity.setNombre(dominio.getNombre());
        entity.setDescripcion(dominio.getDescripcion());
        // No mapeamos productos para evitar problemas de persistencia
        
        return entity;
    }
    
    /**
     * Convierte lista de Entity a lista de Dominio
     */
    public static List<Categoria> aDominioList(List<CategoriaEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(CategoriaMapper::aDominio)
                .collect(Collectors.toList());
    }
    
    /**
     * Convierte lista de Dominio a lista de Entity
     */
    public static List<CategoriaEntity> aEntityList(List<Categoria> categorias) {
        if (categorias == null || categorias.isEmpty()) {
            return Collections.emptyList();
        }
        return categorias.stream()
                .map(CategoriaMapper::aEntity)
                .collect(Collectors.toList());
    }
}