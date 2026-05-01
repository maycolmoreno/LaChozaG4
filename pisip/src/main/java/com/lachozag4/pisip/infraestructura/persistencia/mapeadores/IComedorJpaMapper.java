package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.lachozag4.pisip.dominio.entidades.Comedor;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.ComedorJpa;

@Mapper(componentModel = "spring")
public interface IComedorJpaMapper {

	Comedor toDomain(ComedorJpa entity);

	ComedorJpa toEntity(Comedor comedor);
}
