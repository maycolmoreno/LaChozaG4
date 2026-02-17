package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.lachozag4.pisip.dominio.entidades.Mesa;
import com.lachozag4.pisip.dominio.repositorios.IMesaRepositorio;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IMesaJpaMapper;
import com.lachozag4.pisip.infraestructura.repositorios.IMesaJpaRepositorio;

public class MesaRepositorioImpl implements IMesaRepositorio {

	private final IMesaJpaRepositorio jpaRepository;
	private final IMesaJpaMapper mapper;

	public MesaRepositorioImpl(IMesaJpaRepositorio jpaRepository, IMesaJpaMapper mapper) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
	}

	@Override
	public Mesa guardar(Mesa mesa) {
		return mapper.toDomain(jpaRepository.save(mapper.toEntity(mesa)));
	}

	@Override
	public Optional<Mesa> buscarPorId(int idmesa) {
		return jpaRepository.findById(idmesa).map(mapper::toDomain);
	}

	@Override
	public Optional<Mesa> buscarPorNumero(int numero) {
		return jpaRepository.findByNumero(numero).map(mapper::toDomain);
	}

	@Override
	public List<Mesa> listarDisponibles() {
		return jpaRepository.findByEstadoTrue().stream().map(mapper::toDomain).toList();
	}

	@Override
	public List<Mesa> listarTodas() {
		return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
	}

	@Override
	public Mesa actualizar(Mesa mesa) {
		return guardar(mesa);
	}

	@Override
	public boolean existePorId(int idmesa) {
		return jpaRepository.existsById(idmesa);
	}

	@Override
	public List<Mesa> listarMesasSinPedidosActivos() {
		return jpaRepository.findMesasSinPedidosActivos().stream().map(mapper::toDomain).toList();
	}

	@Override
	public List<Mesa> listarMesasOcupadas() {
		return jpaRepository.findMesasConPedidosActivos().stream().map(mapper::toDomain).toList();
	}

	@Override
	public void eliminar(int idmesa) {
		if (existePorId(idmesa)) {
			jpaRepository.deleteById(idmesa);
		}
	}

}
