package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import com.lachozag4.pisip.dominio.entidades.Cliente;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.ClienteEntity;

public class ClienteMapper {
    public static com.lachozag4.pisip.dominio.entidades.Cliente aDominio(com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.ClienteEntity entity) {
        if (entity == null) return null;
        com.lachozag4.pisip.dominio.entidades.Cliente dominio = new com.lachozag4.pisip.dominio.entidades.Cliente();
        dominio.setId(entity.getId());
        dominio.setIdentificacion(entity.getIdentificacion());
        dominio.setNombre(entity.getNombre());
        // ... el resto de tus sets
        return dominio;
    }


    public static ClienteEntity aEntity(Cliente dominio) {
        if (dominio == null) return null;

        ClienteEntity entity = new ClienteEntity();
        entity.setId(dominio.getId());
        entity.setIdentificacion(dominio.getIdentificacion());
        entity.setNombre(dominio.getNombre());
        entity.setDireccion(dominio.getDireccion());
        entity.setTelefono(dominio.getTelefono());
        entity.setEmail(dominio.getEmail());

        return entity;
    }
}