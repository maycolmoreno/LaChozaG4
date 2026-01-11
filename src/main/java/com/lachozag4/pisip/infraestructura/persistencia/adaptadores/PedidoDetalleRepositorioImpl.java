package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.PedidoDetalle;
import com.lachozag4.pisip.dominio.repositorios.IPedidoDetalleRepositorio;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.PedidoDetalleJpa;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IPedidoDetalleJpaMapper;
import com.lachozag4.pisip.infraestructura.repositorios.IPedidoDetalleJpaRepositorio;

public class PedidoDetalleRepositorioImpl implements IPedidoDetalleRepositorio {

	private final IPedidoDetalleJpaRepositorio jparepository;
	private final IPedidoDetalleJpaMapper entityMapper;

	public PedidoDetalleRepositorioImpl(IPedidoDetalleJpaRepositorio jparepository,
			IPedidoDetalleJpaMapper entityMapper) {
		this.jparepository = jparepository;
		this.entityMapper = entityMapper;
	}

	@Override
	public PedidoDetalle guardar(PedidoDetalle pedidoDetalle) {
		PedidoDetalleJpa entity = entityMapper.toEntity(pedidoDetalle);
		PedidoDetalleJpa guardado = jparepository.save(entity);
		return entityMapper.toDomain(guardado);
	}

	@Override
	public Optional<PedidoDetalle> BuscarPorId(int id) {
		// TODO Auto-generated method stub
		return jparepository.findById(id).map(entityMapper::toDomain);
	}

	@Override
	public List<PedidoDetalle> listarTodos() {
		// TODO Auto-generated method stub
		return jparepository.findAll().stream().map(entityMapper::toDomain).toList();
	}

	@Override
	public void eliminar(int id) {
		// TODO Auto-generated method stub
		jparepository.deleteById(id);

	}

}
