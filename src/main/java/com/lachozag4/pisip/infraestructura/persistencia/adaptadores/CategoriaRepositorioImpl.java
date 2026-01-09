package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.Categoria;
import com.lachozag4.pisip.dominio.repositorios.ICategoriaRepositorio;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.CategoriaJpa;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.ICategoriaJpaMapper;
import com.lachozag4.pisip.infraestructura.repositorios.ICategoriaJpaRepositorio;

public class CategoriaRepositorioImpl implements ICategoriaRepositorio {

	private final ICategoriaJpaRepositorio jparepository;
	private final ICategoriaJpaMapper entityMapper;

	public CategoriaRepositorioImpl(ICategoriaJpaRepositorio jparepository, ICategoriaJpaMapper entity) {
		this.jparepository = jparepository;
		this.entityMapper = entity;
	}

	@Override
	public Categoria guardar(Categoria categoria) {
		CategoriaJpa entity = entityMapper.toEntity(categoria);
		CategoriaJpa guardado = jparepository.save(entity);
		return entityMapper.toDomain(guardado);
	}

	@Override
	public Optional<Categoria> BuscarPorId(int id) {
		// TODO Auto-generated method stub
		return jparepository.findById(id).map(entityMapper::toDomain);
	}

	@Override
	public List<Categoria> listarTodos() {
		// TODO Auto-generated method stub
		return jparepository.findAll().stream().map(entityMapper::toDomain).toList();
	}

	@Override
	public void eliminar(int id) {
		// TODO Auto-generated method stub
		jparepository.deleteById(id);

	}

}
