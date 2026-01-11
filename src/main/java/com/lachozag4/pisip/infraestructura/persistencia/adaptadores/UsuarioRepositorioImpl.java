package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.Usuario;
import com.lachozag4.pisip.dominio.repositorios.IUsuarioRepositorio;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.UsuarioJpa;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IUsuarioJpaMapper;
import com.lachozag4.pisip.infraestructura.repositorios.IUsuarioJpaRepositorio;

public class UsuarioRepositorioImpl implements IUsuarioRepositorio {
	private final IUsuarioJpaRepositorio jparepository;
	private final IUsuarioJpaMapper entityMapper;

	public UsuarioRepositorioImpl(IUsuarioJpaRepositorio jparepository, IUsuarioJpaMapper entityMapper) {
		this.jparepository = jparepository;
		this.entityMapper = entityMapper;
	}

	@Override
	public Usuario guardar(Usuario usuario) {
		UsuarioJpa entity = entityMapper.toEntity(usuario);
		UsuarioJpa guardado = jparepository.save(entity);
		return entityMapper.toDomain(guardado);
	}

	@Override
	public Optional<Usuario> BuscarPorId(int id) {
		// TODO Auto-generated method stub
		return jparepository.findById(id).map(entityMapper::toDomain);
	}

	@Override
	public List<Usuario> listarTodos() {
		// TODO Auto-generated method stub
		return jparepository.findAll().stream().map(entityMapper::toDomain).toList();
	}

	@Override
	public void eliminar(int id) {
		// TODO Auto-generated method stub
		jparepository.deleteById(id);

	}

}
