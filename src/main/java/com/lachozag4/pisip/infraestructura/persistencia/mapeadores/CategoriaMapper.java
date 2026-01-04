package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import com.lachozag4.pisip.dominio.entidades.Categoria;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.CategoriaEntity;

public class CategoriaMapper {
    
    public static Categoria aDominio(CategoriaEntity entity) {
        if (entity == null) return null;
        
        Categoria dominio = new Categoria();
        dominio.setId(entity.getId());
        dominio.setNombre(entity.getNombre());
        dominio.setDescripcion(entity.getDescripcion());
        // No mapeamos productos para evitar lazy loading
        
        return dominio;
    }
    
    public static CategoriaEntity aEntity(Categoria dominio) {
        if (dominio == null) return null;
        
        CategoriaEntity entity = new CategoriaEntity();
        entity.setId(dominio.getId());
        entity.setNombre(dominio.getNombre());
        entity.setDescripcion(dominio.getDescripcion());
        // No mapeamos productos
        
        return entity;
    }
}