package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.lachozag4.pisip.dominio.entidades.Cuenta;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.CuentaJpa;

@Mapper(componentModel = "spring", uses = { IMesaJpaMapper.class, IClienteJpaMapper.class })
public interface ICuentaJpaMapper {

	@org.mapstruct.Mapping(target = "conTotal", ignore = true)
	Cuenta toDomain(CuentaJpa entity);

	CuentaJpa toEntity(Cuenta domain);
}
