package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.lachozag4.pisip.dominio.entidades.Usuario;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.UsuarioJpa;

@Mapper(componentModel = "spring")
public interface IUsuarioJpaMapper {
	Usuario toDomain(UsuarioJpa entity);

	UsuarioJpa toEntity(Usuario usuario);

}
