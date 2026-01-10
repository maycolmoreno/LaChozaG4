package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.Cliente;
import com.lachozag4.pisip.dominio.repositorios.IClienteRepositorio;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.ClienteJpa;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IClienteJpaMapper;
import com.lachozag4.pisip.infraestructura.repositorios.IClienteJpaRepositorio;

public class ClienteRepositorioImpl implements IClienteRepositorio {

	private final IClienteJpaRepositorio jparepository;
	private final IClienteJpaMapper entityMapper;

	public ClienteRepositorioImpl(IClienteJpaRepositorio jparepository, IClienteJpaMapper entityMapper) {
		super();
		this.jparepository = jparepository;
		this.entityMapper = entityMapper;
	}

	@Override
	public Cliente guardar(Cliente cliente) {
		ClienteJpa entity = entityMapper.toEntity(cliente);
		ClienteJpa guardado = jparepository.save(entity);
		return entityMapper.toDomain(guardado);
	}

	@Override
	public Optional<Cliente> BuscarPorId(int id) {
		// TODO Auto-generated method stub
		return jparepository.findById(id).map(entityMapper::toDomain);
	}

	@Override
	public List<Cliente> listarTodos() {
		// TODO Auto-generated method stub
		return jparepository.findAll().stream().map(entityMapper::toDomain).toList();
	}

	@Override
	public void eliminar(int id) {
		// TODO Auto-generated method stub
		jparepository.deleteById(id);

	}

}
