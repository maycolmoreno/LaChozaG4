package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import com.lachozag4.pisip.dominio.entidades.Cliente;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.ClienteEntity;

public class ClienteMapper {

    public static Cliente aDominio(ClienteEntity entity) {
        if (entity == null) return null;
        Cliente dominio = new Cliente();
        dominio.setId(entity.getId());
        dominio.setIdentificacion(entity.getIdentificacion());
        dominio.setNombre(entity.getNombre());
        dominio.setDireccion(entity.getDireccion()); // Asegúrate de agregar esta línea
        dominio.setTelefono(entity.getTelefono());   // Asegúrate de agregar esta línea
        dominio.setEmail(entity.getEmail());         // Asegúrate de agregar esta línea
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