package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.PedidoDetalle;
import com.lachozag4.pisip.dominio.repositorios.IPedidoDetalleRepositorio;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IPedidoDetalleJpaMapper;
import com.lachozag4.pisip.infraestructura.repositorios.IPedidoDetalleJpaRepositorio;

public class PedidoDetalleRepositorioImpl implements IPedidoDetalleRepositorio {

	private final IPedidoDetalleJpaRepositorio jpaRepository;
	private final IPedidoDetalleJpaMapper mapper;

	public PedidoDetalleRepositorioImpl(IPedidoDetalleJpaRepositorio jpaRepository, IPedidoDetalleJpaMapper mapper) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
	}

	@Override
	public PedidoDetalle guardar(PedidoDetalle detalle) {
		return mapper.toDomain(jpaRepository.save(mapper.toEntity(detalle)));
	}

	@Override
	public Optional<PedidoDetalle> buscarPorId(int id) {
		return jpaRepository.findById(id).map(mapper::toDomain);
	}

	@Override
	public List<PedidoDetalle> listarTodos() {
		return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
	}

	@Override
	public List<PedidoDetalle> listarPorPedido(int idPedido) {
		return jpaRepository.findByFkPedido_Idpedido(idPedido).stream().map(mapper::toDomain).toList();
	}
	@Override
	public PedidoDetalle actualizar(PedidoDetalle detalle) {
		return guardar(detalle);
	}

	@Override
	public boolean existePorId(int id) {
		return jpaRepository.existsById(id);
	}

	@Override
	public void eliminar(int id) {
		if (existePorId(id)) {
			jpaRepository.deleteById(id);
		}
	}
}


