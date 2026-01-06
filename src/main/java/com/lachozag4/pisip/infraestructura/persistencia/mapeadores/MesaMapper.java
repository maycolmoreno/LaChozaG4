package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import com.lachozag4.pisip.dominio.entidades.Mesa;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.MesaEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MesaMapper {
    
    /**
     * Convierte de Entity a Dominio
     */
    public static Mesa aDominio(MesaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Mesa dominio = new Mesa();
        dominio.setId(entity.getId());
        dominio.setNumero(entity.getNumero());
        dominio.setCapacidad(entity.getCapacidad());
        dominio.setEstado(entity.getEstado());
        dominio.setUbicacion(entity.getUbicacion());
        
        return dominio;
    }
    
    /**
     * Convierte de Dominio a Entity
     */
    public static MesaEntity aEntity(Mesa dominio) {
        if (dominio == null) {
            return null;
        }
        
        MesaEntity entity = new MesaEntity();
        entity.setId(dominio.getId());
        entity.setNumero(dominio.getNumero());
        entity.setCapacidad(dominio.getCapacidad());
        entity.setEstado(dominio.getEstado());
        entity.setUbicacion(dominio.getUbicacion());
        
        return entity;
    }
    
    /**
     * Convierte lista de Entity a lista de Dominio
     */
    public static List<Mesa> aDominioList(List<MesaEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(MesaMapper::aDominio)
                .collect(Collectors.toList());
    }
    
    /**
     * Convierte lista de Dominio a lista de Entity
     */
    public static List<MesaEntity> aEntityList(List<Mesa> mesas) {
        if (mesas == null || mesas.isEmpty()) {
            return Collections.emptyList();
        }
        return mesas.stream()
                .map(MesaMapper::aEntity)
                .collect(Collectors.toList());
    }
}