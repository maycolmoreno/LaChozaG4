package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import com.lachozag4.pisip.dominio.entidades.Mesa;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.MesaEntity;

public class MesaMapper {
    public static com.lachozag4.pisip.dominio.entidades.Mesa aDominio(com.lachozag4.pisip.infraestructura.persistencia.jpa.entidades.MesaEntity entity) {
        if (entity == null) return null;
        com.lachozag4.pisip.dominio.entidades.Mesa dominio = new com.lachozag4.pisip.dominio.entidades.Mesa();
        dominio.setId(entity.getId());
        dominio.setNumero(entity.getNumero());
        dominio.setCapacidad(entity.getCapacidad());
        dominio.setEstado(entity.getEstado());
        dominio.setUbicacion(entity.getUbicacion());
        return dominio;
    }


    public static MesaEntity aEntity(Mesa dominio) {
        if (dominio == null) return null;

        MesaEntity entity = new MesaEntity();
        entity.setId(dominio.getId());
        entity.setNumero(dominio.getNumero());
        entity.setCapacidad(dominio.getCapacidad());
        entity.setEstado(dominio.getEstado());
        entity.setUbicacion(dominio.getUbicacion());

        return entity;
    }
}