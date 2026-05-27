package com.lachozag4.pisip.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.lachozag4.pisip.dominio.entidades.Cuenta;
import com.lachozag4.pisip.dominio.enums.EstadoCuenta;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.CuentaJpa;

@Mapper(componentModel = "spring", uses = { IMesaJpaMapper.class, IClienteJpaMapper.class })
public interface ICuentaJpaMapper {

	@org.mapstruct.Mapping(target = "conTotal", ignore = true)
	@Mapping(target = "estado", expression = "java(entity.getEstado() != null ? entity.getEstado().name() : null)")
	Cuenta toDomain(CuentaJpa entity);

	@Mapping(target = "estado", expression = "java(domain.getEstado() != null ? com.lachozag4.pisip.dominio.enums.EstadoCuenta.valueOf(domain.getEstado()) : null)")
	CuentaJpa toEntity(Cuenta domain);

	default String estadoToString(EstadoCuenta e) { return e == null ? null : e.name(); }
	default EstadoCuenta stringToEstado(String s) { return s == null ? null : EstadoCuenta.valueOf(s); }
}
