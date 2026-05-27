package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.dominio.entidades.ResultadoPaginado;
import com.lachozag4.pisip.dominio.enums.EstadoPedido;
import com.lachozag4.pisip.dominio.repositorios.IPedidoRepositorio;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.PedidoJpa;
import com.lachozag4.pisip.infraestructura.persistencia.mapeadores.IPedidoJpaMapper;
import com.lachozag4.pisip.infraestructura.repositorios.IPedidoJpaRepositorio;

public class PedidoRepositorioImpl implements IPedidoRepositorio {

	private final IPedidoJpaRepositorio jpaRepository;
	private final IPedidoJpaMapper mapper;

	public PedidoRepositorioImpl(IPedidoJpaRepositorio jpaRepository, IPedidoJpaMapper mapper) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
	}

	@Override
	public Pedido guardar(Pedido pedido) {
		return mapper.toDomain(jpaRepository.save(mapper.toEntity(pedido)));
	}

	@Override
	public Optional<Pedido> buscarPorId(int id) {
		return jpaRepository.findById(id).map(mapper::toDomain);
	}

	@Override
	public List<Pedido> listarTodos() {
		return jpaRepository.findAllWithRelations().stream().map(mapper::toDomain).toList();
	}

	@Override
	public List<Pedido> listarPendientes() {
		return jpaRepository.findByEstado(EstadoPedido.PENDIENTE).stream().map(mapper::toDomain).toList();
	}

	@Override
	public List<Pedido> listarCompletados() {
		return jpaRepository.findByEstado(EstadoPedido.COMPLETADO).stream().map(mapper::toDomain).toList();
	}

	@Override
	public Pedido actualizar(Pedido pedido) {
		return guardar(pedido);
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

	@Override
	public List<Pedido> buscarPedidosActivosPorMesa(int idMesa) {
		return jpaRepository.findPedidosActivosByMesa(idMesa).stream().map(mapper::toDomain).toList();
	}

	@Override
	public boolean existePedidoActivoPorMesa(int idMesa, int excluirIdPedido) {
		return jpaRepository.existePedidoActivoPorMesaExcluyendo(idMesa, excluirIdPedido);
	}

	@Override
	public List<Pedido> listarCompletadosPorFecha(LocalDate fecha) {
		LocalDateTime inicio = fecha.atStartOfDay();
		LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
		return jpaRepository.findCompletadosBetween(inicio, fin)
				.stream().map(mapper::toDomain).toList();
	}

	@Override
	public List<Pedido> listarPorCuenta(int idcuenta) {
		return jpaRepository.findByFkCuenta_Idcuenta(idcuenta).stream().map(mapper::toDomain).toList();
	}

	@Override
	public ResultadoPaginado<Pedido> listarPaginado(String estado, String q,
			LocalDateTime fechaDesde, LocalDateTime fechaHasta,
			int page, int size) {
		PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fecha"));
		Page<PedidoJpa> pageResult = jpaRepository.buscarPaginado(estado, q, fechaDesde, fechaHasta, pageable);

		List<Pedido> pedidos = pageResult.getContent().stream()
				.map(mapper::toDomain)
				.toList();

		return new ResultadoPaginado<>(
				pedidos,
				pageResult.getTotalElements(),
				pageResult.getTotalPages(),
				pageResult.getNumber(),
				pageResult.getSize()
		);
	}
}
