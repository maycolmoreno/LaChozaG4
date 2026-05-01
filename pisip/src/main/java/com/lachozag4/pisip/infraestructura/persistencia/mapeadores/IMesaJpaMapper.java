package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.lachozag4.pisip.dominio.entidades.Mesa;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.ComedorJpa;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.MesaJpa;

@Mapper(componentModel = "spring")
public interface IMesaJpaMapper {

    @Mapping(target = "idcomedor", source = "fkComedor.idcomedor")
    Mesa toDomain(MesaJpa mesaJpa);

    @Mapping(target = "fkComedor", source = "idcomedor", qualifiedByName = "idToComedor")
    MesaJpa toEntity(Mesa mesa);

    @Named("idToComedor")
    default ComedorJpa idToComedor(Integer idcomedor) {
        if (idcomedor == null) return null;
        ComedorJpa comedor = new ComedorJpa();
        comedor.setIdcomedor(idcomedor);
        return comedor;
    }
}

