package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import com.lachozag4.pisip.dominio.entidades.Cliente;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.ClienteEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClienteMapper {
    
    /**
     * Convierte de Entity a Dominio
     */
    public static Cliente aDominio(ClienteEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Cliente dominio = new Cliente();
        dominio.setId(entity.getId());
        dominio.setIdentificacion(entity.getIdentificacion());
        dominio.setNombre(entity.getNombre());
        dominio.setDireccion(entity.getDireccion());
        dominio.setTelefono(entity.getTelefono());
        dominio.setEmail(entity.getEmail());
        
        return dominio;
    }
    
    /**
     * Convierte de Dominio a Entity
     */
    public static ClienteEntity aEntity(Cliente dominio) {
        if (dominio == null) {
            return null;
        }
        
        ClienteEntity entity = new ClienteEntity();
        entity.setId(dominio.getId());
        entity.setIdentificacion(dominio.getIdentificacion());
        entity.setNombre(dominio.getNombre());
        entity.setDireccion(dominio.getDireccion());
        entity.setTelefono(dominio.getTelefono());
        entity.setEmail(dominio.getEmail());
        
        return entity;
    }
    
    /**
     * Convierte lista de Entity a lista de Dominio
     */
    public static List<Cliente> aDominioList(List<ClienteEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(ClienteMapper::aDominio)
                .collect(Collectors.toList());
    }
    
    /**
     * Convierte lista de Dominio a lista de Entity
     */
    public static List<ClienteEntity> aEntityList(List<Cliente> clientes) {
        if (clientes == null || clientes.isEmpty()) {
            return Collections.emptyList();
        }
        return clientes.stream()
                .map(ClienteMapper::aEntity)
                .collect(Collectors.toList());
    }
}