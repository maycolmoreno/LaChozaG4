package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.Comedor;
import com.lachozag4.pisip.dominio.repositorios.IComedorRepositorio;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IComedorJpaMapper;
import com.lachozag4.pisip.infraestructura.repositorios.IComedorJpaRepositorio;

public class ComedorRepositorioImpl implements IComedorRepositorio {

	private final IComedorJpaRepositorio jpaRepository;
	private final IComedorJpaMapper mapper;

	public ComedorRepositorioImpl(IComedorJpaRepositorio jpaRepository, IComedorJpaMapper mapper) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
	}

	@Override
	public Comedor guardar(Comedor comedor) {
		return mapper.toDomain(jpaRepository.save(mapper.toEntity(comedor)));
	}

	@Override
	public Optional<Comedor> buscarPorId(int idcomedor) {
		return jpaRepository.findById(idcomedor).map(mapper::toDomain);
	}

	@Override
	public Optional<Comedor> buscarPorNombre(String nombre) {
		return jpaRepository.findByNombre(nombre).map(mapper::toDomain);
	}

	@Override
	public List<Comedor> listarActivos() {
		return jpaRepository.findByEstadoTrue().stream().map(mapper::toDomain).toList();
	}

	@Override
	public List<Comedor> listarTodos() {
		return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
	}

	@Override
	public Comedor actualizar(Comedor comedor) {
		return guardar(comedor);
	}

	@Override
	public void eliminar(int idcomedor) {
		jpaRepository.deleteById(idcomedor);
	}
}
