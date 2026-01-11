package com.lachozag4.pisip.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.lachozag4.pisip.dominio.entidades.Usuario;
import com.lachozag4.pisip.presentacion.dto.request.UsuarioRequestDTO;
import com.lachozag4.pisip.presentacion.dto.response.UsuarioResponseDTO;

@Mapper(componentModel = "spring")
public interface IUsuarioDtoMapper {
	Usuario toDomain(UsuarioRequestDTO dto);

	UsuarioResponseDTO toResponseDTO(Usuario usuario);

}
