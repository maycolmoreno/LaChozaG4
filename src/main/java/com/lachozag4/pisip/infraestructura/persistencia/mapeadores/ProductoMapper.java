package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.ProductoEntity;

public class ProductoMapper {

    public static Producto aDominio(ProductoEntity entity) {
        if (entity == null) return null;

        Producto dominio = new Producto();
        dominio.setId(entity.getId());
        dominio.setNombre(entity.getNombre());
        dominio.setPrecio(entity.getPrecio());
        dominio.setStockActual(entity.getStockActual());
        dominio.setDescripcion(entity.getDescripcion());

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

        return entity;
    }
}
