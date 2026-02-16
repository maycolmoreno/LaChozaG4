package com.lachozag4.pisip.presentacion.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.lachozag4.pisip.dominio.entidades.Cuenta;
import com.lachozag4.pisip.presentacion.dto.response.CuentaResponseDTO;

@Mapper(componentModel = "spring", uses = { IMesaDtoMapper.class, IClienteDtoMapper.class })
public interface ICuentaDtoMapper {

	@Mapping(target = "idcuenta", source = "idcuenta")
	@Mapping(target = "fechaApertura", source = "fechaApertura")
	@Mapping(target = "fechaCierre", source = "fechaCierre")
	@Mapping(target = "estado", source = "estado")
	@Mapping(target = "total", source = "total")
	@Mapping(target = "fkMesa", source = "fkMesa")
	@Mapping(target = "fkCliente", source = "fkCliente")
	CuentaResponseDTO toResponseDTO(Cuenta cuenta);
}
