package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;



import com.lachozag4.pisip.dominio.entidades.Usuario;
import com.lachozag4.pisip.dominio.repositorios.IUsuarioRepositorio;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IUsuarioJpaMapper;
import com.lachozag4.pisip.infraestructura.repositorios.IUsuarioJpaRepositorio;


public class UsuarioRepositorioImpl implements IUsuarioRepositorio {

	private final IUsuarioJpaRepositorio jpaRepository;
	private final IUsuarioJpaMapper mapper;

	public UsuarioRepositorioImpl(IUsuarioJpaRepositorio jpaRepository, IUsuarioJpaMapper mapper) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
	}

	@Override
	public Usuario guardar(Usuario usuario) {
		return mapper.toDomain(jpaRepository.save(mapper.toEntity(usuario)));
	}

	@Override
	public Optional<Usuario> buscarPorId(int idusuario) {
		return jpaRepository.findById(idusuario).map(mapper::toDomain);
	}

	@Override
	public Optional<Usuario> buscarPorUsername(String username) {
		return jpaRepository.findByUsername(username).map(mapper::toDomain);
	}

	@Override
	public List<Usuario> listarDisponibles() {
		return jpaRepository.findByEstadoTrue().stream().map(mapper::toDomain).toList();
	}
	@Override
	public List<Usuario> listarTodos() {
		return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
	}
	@Override
	public Usuario actualizar(Usuario usuario) {
		return guardar(usuario);
	}
	@Override
	public boolean existePorId(int idusuario) {
		return jpaRepository.existsById(idusuario);
	}

	@Override
	public void eliminar(int idusuario) {
		if (existePorId(idusuario)) {
			jpaRepository.deleteById(idusuario);
		}
	}
}
