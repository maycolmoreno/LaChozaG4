package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;


import com.lachozag4.pisip.dominio.entidades.Cliente;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.ClienteJpa;

@Mapper(componentModel = "spring")
public interface IClienteJpaMapper {
	Cliente toDomain(ClienteJpa entity);
	ClienteJpa toEntity(Cliente cliente);

}
