package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.lachozag4.pisip.dominio.entidades.Categoria;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.CategoriaJpa;

@Mapper(componentModel = "spring")
public interface ICategoriaJpaMapper {

	Categoria toDomain(CategoriaJpa entity);
	CategoriaJpa toEntity(Categoria categoria);
}
