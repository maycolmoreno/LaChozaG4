package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.CajaTurno;
import com.lachozag4.pisip.dominio.repositorios.ICajaTurnoRepositorio;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.CajaTurnoJpa;
import com.lachozag4.pisip.infraestructura.repositorios.ICajaTurnoJpaRepositorio;

public class CajaTurnoRepositorioImpl implements ICajaTurnoRepositorio {

	private final ICajaTurnoJpaRepositorio jpaRepositorio;

	public CajaTurnoRepositorioImpl(ICajaTurnoJpaRepositorio jpaRepositorio) {
		this.jpaRepositorio = jpaRepositorio;
	}

	@Override
	public CajaTurno guardar(CajaTurno cajaTurno) {
		CajaTurnoJpa entity = toEntity(cajaTurno);
		CajaTurnoJpa saved = jpaRepositorio.save(entity);
		return toDomain(saved);
	}

	@Override
	public Optional<CajaTurno> buscarCajaAbierta() {
		return jpaRepositorio.findFirstByEstadoOrderByIdcajaDesc(CajaTurno.ESTADO_ABIERTA).map(this::toDomain);
	}

	@Override
	public Optional<CajaTurno> buscarPorId(int idcaja) {
		return jpaRepositorio.findById(idcaja).map(this::toDomain);
	}

	@Override
	public List<CajaTurno> listarTodos() {
		return jpaRepositorio.findAll().stream().map(this::toDomain).toList();
	}

	private CajaTurnoJpa toEntity(CajaTurno domain) {
		CajaTurnoJpa entity = new CajaTurnoJpa();
		entity.setIdcaja(domain.getIdcaja());
		entity.setFechaApertura(domain.getFechaApertura());
		entity.setFechaCierre(domain.getFechaCierre());
		entity.setMontoInicial(domain.getMontoInicial());
		entity.setMontoEsperadoCierre(domain.getMontoEsperadoCierre());
		entity.setMontoDeclaradoCierre(domain.getMontoDeclaradoCierre());
		entity.setDiferencia(domain.getDiferencia());
		entity.setEstado(domain.getEstado());
		entity.setUsuarioApertura(domain.getUsuarioApertura());
		entity.setUsuarioCierre(domain.getUsuarioCierre());
		entity.setObservaciones(domain.getObservaciones());
		return entity;
	}

	private CajaTurno toDomain(CajaTurnoJpa entity) {
		return new CajaTurno(entity.getIdcaja(), entity.getFechaApertura(), entity.getFechaCierre(),
				entity.getMontoInicial(), entity.getMontoEsperadoCierre(), entity.getMontoDeclaradoCierre(),
				entity.getDiferencia(), entity.getEstado(), entity.getUsuarioApertura(), entity.getUsuarioCierre(),
				entity.getObservaciones());
	}
}
