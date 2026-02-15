package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.Categoria;
import com.lachozag4.pisip.dominio.repositorios.ICategoriaRepositorio;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.ICategoriaJpaMapper;
import com.lachozag4.pisip.infraestructura.repositorios.ICategoriaJpaRepositorio;

public class CategoriaRepositorioImpl implements ICategoriaRepositorio {

	private final ICategoriaJpaRepositorio jpaRepository;
	private final ICategoriaJpaMapper mapper;

	public CategoriaRepositorioImpl(ICategoriaJpaRepositorio jpaRepository, ICategoriaJpaMapper mapper) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
	}

	@Override
	public Categoria guardar(Categoria categoria) {
		return mapper.toDomain(jpaRepository.save(mapper.toEntity(categoria)));
	}

	@Override
	public Optional<Categoria> buscarPorId(int idcategoria) {
		return jpaRepository.findById(idcategoria).map(mapper::toDomain);
	}

	@Override
	public Optional<Categoria> buscarPorNombre(String nombre) {
		return jpaRepository.findByNombre(nombre).map(mapper::toDomain);
	}

	@Override
	public List<Categoria> listarActivas() {
		return jpaRepository.findByEstadoTrue().stream().map(mapper::toDomain).toList();
	}

	@Override
	public List<Categoria> listarTodas() {
		return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
	}

	@Override
	public Categoria actualizar(Categoria categoria) {
		return guardar(categoria);
	}

	@Override
	public void eliminar(int idcategoria) {

		jpaRepository.deleteById(idcategoria);

	}
}
