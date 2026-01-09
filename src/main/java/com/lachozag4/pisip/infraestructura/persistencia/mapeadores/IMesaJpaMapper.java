package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.lachozag4.pisip.dominio.entidades.Mesa;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.MesaJpa;

@Mapper(componentModel = "spring")
public interface IMesaJpaMapper {
	Mesa toDomain(MesaJpa entity);
	MesaJpa toEntity(Mesa mesa);

}
