package com.lachozag4.pisip.aplicacion.casosuso.impl;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IPedidoUseCase;
import com.lachozag4.pisip.aplicacion.excepciones.BusinessException;
import com.lachozag4.pisip.aplicacion.excepciones.NotFoundException;
import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.dominio.entidades.Cuenta;
import com.lachozag4.pisip.dominio.entidades.ResultadoPaginado;
import com.lachozag4.pisip.dominio.repositorios.IPedidoRepositorio;
import com.lachozag4.pisip.dominio.repositorios.ICuentaRepositorio;
import com.lachozag4.pisip.dominio.servicios.IGestionStockServicio;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PedidoUseCaseImpl implements IPedidoUseCase {

	private final IPedidoRepositorio repositorio;
	private final IGestionStockServicio stockServicio;
    private final ICuentaRepositorio cuentaRepositorio;

	@Override
	@Transactional
	public Pedido crear(Pedido pedido) {
		// Ya no restringimos por pedidos activos en la misma mesa.
		// La mesa puede tener varios pedidos abiertos simultáneamente.
		stockServicio.validarYDescontar(pedido.getDetalles());
		return repositorio.guardar(pedido.comoPendiente());
	}

	@Override
	public Pedido obtenerPorId(int id) {
		return repositorio.buscarPorId(id)
				.orElseThrow(() -> new NotFoundException("Pedido no encontrado con ID: " + id));
	}

	@Override
	public List<Pedido> listar() {
		return repositorio.listarTodos();
	}

	@Override
	@Transactional
	public Pedido actualizar(int id, Pedido pedido) {
		Pedido existente = obtenerPorId(id);
		validarEditable(existente);
		validarCuentaNoCerrada(existente);

		// Orden seguro bajo @Transactional: si algo falla, se revierte todo
		stockServicio.restaurar(existente.getDetalles());
		stockServicio.validarYDescontar(pedido.getDetalles());

		Pedido actualizado = existente.conDatosActualizados(id, pedido.getFecha(), pedido.getObservaciones(),
				pedido.getFkUsuario(), pedido.getFkMesa(), pedido.getFkCliente(), pedido.getDetalles());
		Pedido guardado = repositorio.guardar(actualizado);

		// Si el pedido pertenece a una cuenta, recalcular el total de esa cuenta
		recalcularTotalCuentaSiAplica(guardado);
		return guardado;
	}

	@Override
	@Transactional
	public Pedido cambiarEstado(int id, String nuevoEstado) {
		Pedido existente = obtenerPorId(id);

		validarCuentaNoCerrada(existente);

		if (!existente.puedeTransicionarA(nuevoEstado)) {
			throw new BusinessException("Transición de estado no permitida para pedido #" + id + ": "
					+ existente.getEstado() + " → " + nuevoEstado);
		}

		if (Pedido.ESTADO_EN_COCINA.equals(nuevoEstado)) {
			stockServicio.validarProductosActivos(existente.getDetalles());
		}

		if (Pedido.ESTADO_CANCELADO.equals(nuevoEstado)) {
			stockServicio.restaurar(existente.getDetalles());
		}

		return repositorio.guardar(existente.conEstado(nuevoEstado));
	}

	@Override
	@Transactional
	public void eliminar(int id) {
		Pedido pedido = obtenerPorId(id);
		validarCuentaNoCerrada(pedido);

		if (pedido.esEstadoFinal()) {
			throw new BusinessException("No se puede eliminar un pedido en estado " + pedido.getEstado());
			// O hacerlo idempotente: return;
		}

		stockServicio.restaurar(pedido.getDetalles());
		Pedido cancelado = repositorio.guardar(pedido.conEstado(Pedido.ESTADO_CANCELADO));
		recalcularTotalCuentaSiAplica(cancelado);
	}

	// ── Validaciones privadas ──

	@Override
	public ResultadoPaginado<Pedido> listarPaginado(String estado, String q,
			LocalDateTime fechaDesde, LocalDateTime fechaHasta,
			int page, int size) {
		return repositorio.listarPaginado(estado, q, fechaDesde, fechaHasta, page, size);
	}

	private void recalcularTotalCuentaSiAplica(Pedido pedido) {
		if (pedido.getFkCuenta() == null) {
			return;
		}

		int idCuenta = pedido.getFkCuenta().getIdcuenta();
		List<Pedido> pedidosCuenta = repositorio.listarPorCuenta(idCuenta);
		// Solo cuentan para el total los pedidos que no están cancelados
		double total = pedidosCuenta.stream()
				.filter(p -> !Pedido.ESTADO_CANCELADO.equals(p.getEstado()))
				.flatMap(p -> p.getDetalles().stream())
				.mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
				.sum();

		boolean todosCancelados = !pedidosCuenta.isEmpty()
				&& pedidosCuenta.stream().allMatch(p -> Pedido.ESTADO_CANCELADO.equals(p.getEstado()));

		var cuenta = pedido.getFkCuenta();
		if (todosCancelados) {
			// Si todos los pedidos de la cuenta fueron cancelados, anulamos la cuenta
			var cuentaCerrada = cuenta.conEstado(Cuenta.ESTADO_ANULADA, LocalDateTime.now()).conTotal(0.0);
			cuentaRepositorio.actualizar(cuentaCerrada);
		} else {
			var cuentaActualizada = cuenta.conTotal(total);
			cuentaRepositorio.actualizar(cuentaActualizada);
		}
	}

	private void validarEditable(Pedido pedido) {
		if (!pedido.esEditable()) {
			throw new BusinessException(
					"Este pedido no se puede modificar porque está en estado " + pedido.getEstado());
		}
	}

	/**
	 * Evita modificar pedidos ligados a una cuenta ya pagada/anulada.
	 */
	private void validarCuentaNoCerrada(Pedido pedido) {
		if (pedido.getFkCuenta() != null && pedido.getFkCuenta().estaCerrada()) {
			throw new BusinessException("No se puede modificar este pedido porque su cuenta ya está "
					+ pedido.getFkCuenta().getEstado());
		}
	}
}