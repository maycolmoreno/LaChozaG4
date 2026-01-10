package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.Mesa;
import com.lachozag4.pisip.dominio.repositorios.IMesaRepositorio;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.MesaJpa;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IMesaJpaMapper;
import com.lachozag4.pisip.infraestructura.repositorios.IMesaJpaRepositorio;

public class MesaRepositorioImpl implements IMesaRepositorio {

	private final IMesaJpaRepositorio jparepository;
	private final IMesaJpaMapper entityMapper;

	public MesaRepositorioImpl(IMesaJpaRepositorio jparepository, IMesaJpaMapper entityMapper) {
		this.jparepository = jparepository;
		this.entityMapper = entityMapper;
	}

	@Override
	public Mesa guardar(Mesa mesa) {
		MesaJpa entity = entityMapper.toEntity(mesa);
		MesaJpa guardado = jparepository.save(entity);
		return entityMapper.toDomain(guardado);
	}

	@Override
	public Optional<Mesa> BuscarPorId(int id) {
		// TODO Auto-generated method stub
		return jparepository.findById(id).map(entityMapper::toDomain);
	}

	@Override
	public List<Mesa> listarTodos() {
		// TODO Auto-generated method stub
		return jparepository.findAll().stream().map(entityMapper::toDomain).toList();
	}

	@Override
	public void eliminar(int id) {
		// TODO Auto-generated method stub
		jparepository.deleteById(id);

	}

}
