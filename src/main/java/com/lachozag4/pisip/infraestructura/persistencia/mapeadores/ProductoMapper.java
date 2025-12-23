package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.ProductoEntity;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.CategoriaEntity; // 👈 Importante

public class ProductoMapper {

    public static Producto aDominio(ProductoEntity entity) {
        if (entity == null) return null;

        Producto dominio = new Producto();
        dominio.setId(entity.getId());
        dominio.setNombre(entity.getNombre());
        dominio.setPrecio(entity.getPrecio());
        dominio.setStockActual(entity.getStockActual());
        dominio.setDescripcion(entity.getDescripcion());
        
        // 👈 MAPEO DE CATEGORÍA (Hacia Dominio)
        if (entity.getCategoria() != null) {
            dominio.setCategoria(CategoriaMapper.aDominio(entity.getCategoria()));
        }

        return dominio;
    }

    public static ProductoEntity aEntity(Producto dominio) {
        if (dominio == null) return null;

        ProductoEntity entity = new ProductoEntity();
        entity.setId(dominio.getId());
        entity.setNombre(dominio.getNombre());
        entity.setPrecio(dominio.getPrecio());
        entity.setStockActual(dominio.getStockActual());
        entity.setDescripcion(dominio.getDescripcion());
         // Asegura que se guarde como disponible
        entity.setActivo(true);     // Asegura que se guarde como activo

        // 👈 MAPEO DE CATEGORÍA (Hacia Base de Datos)
        // Esto es lo que soluciona el error "not-null property references a null"
        if (dominio.getCategoria() != null) {
            CategoriaEntity catEntity = new CategoriaEntity();
            catEntity.setId(dominio.getCategoria().getId());
            entity.setCategoria(catEntity);
        }

        return entity;
    }
}