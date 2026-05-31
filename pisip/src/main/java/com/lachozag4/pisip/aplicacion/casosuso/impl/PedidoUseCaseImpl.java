package com.lachozag4.pisip.aplicacion.casosuso.impl;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IPedidoUseCase;
import com.lachozag4.pisip.aplicacion.excepciones.BusinessException;
import com.lachozag4.pisip.aplicacion.excepciones.NotFoundException;
import com.lachozag4.pisip.aplicacion.servicios.PedidoHistorialService;
import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.dominio.entidades.Cuenta;
import com.lachozag4.pisip.dominio.entidades.ResultadoPaginado;
import com.lachozag4.pisip.dominio.repositorios.IPedidoRepositorio;
import com.lachozag4.pisip.dominio.repositorios.ICuentaRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IMesaRepositorio;
import com.lachozag4.pisip.dominio.servicios.IGestionStockServicio;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PedidoUseCaseImpl implements IPedidoUseCase {

	private final IPedidoRepositorio repositorio;
	private final IGestionStockServicio stockServicio;
    private final ICuentaRepositorio cuentaRepositorio;
    private final IMesaRepositorio mesaRepositorio;
    private final PedidoHistorialService historialService;

	@Override
	@Transactional
	public Pedido crear(Pedido pedido) {
		// Ya no restringimos por pedidos activos en la misma mesa.
		// La mesa puede tener varios pedidos abiertos simultÃ¡neamente.
		stockServicio.validarProductosActivos(pedido.getDetalles());
		stockServicio.validarYDescontar(pedido.getDetalles());
		Pedido creado = repositorio.guardar(pedido.comoPendiente());
		historialService.registrarCreacion(creado);
		// Marcar la mesa como OCUPADA (estado = false) automáticamente
		if (creado.getFkMesa() != null) {
			mesaRepositorio.buscarPorId(creado.getFkMesa().getIdmesa()).ifPresent(mesa ->
				mesaRepositorio.guardar(mesa.conEstado(false))
			);
		}
		return creado;
	}

	@Override
	@Transactional
	public Pedido crearConCuenta(Pedido pedido, String estadoDestino) {
		Pedido creado = crear(pedido);
		Cuenta cuenta = resolverCuentaParaPedido(creado);
		Pedido asociado = repositorio.actualizar(creado.conCuenta(cuenta));
		recalcularTotalCuentaSiAplica(asociado);

		String estadoNormalizado = normalizarEstadoDestino(estadoDestino);
		if (Pedido.ESTADO_PENDIENTE.equals(estadoNormalizado)) {
			return asociado;
		}

		return cambiarEstado(asociado.getIdpedido(), estadoNormalizado);
	}

	@Override
	public Pedido obtenerPorId(int id) {
		return repositorio.buscarPorId(id)
				.orElseThrow(() -> new NotFoundException("Pedido no encontrado con ID: " + id));
	}

	@Override
	public Pedido obtenerRecientePorCuenta(int idcuenta) {
		return repositorio.listarPorCuenta(idcuenta).stream()
				.sorted((a, b) -> {
					int activos = Boolean.compare(b.getEstado() != null && !b.esEstadoFinal(), a.getEstado() != null && !a.esEstadoFinal());
					if (activos != 0) {
						return activos;
					}
					return b.getFecha().compareTo(a.getFecha());
				})
				.findFirst()
				.orElseThrow(() -> new NotFoundException("No se encontraron pedidos para la cuenta con ID: " + idcuenta));
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
		historialService.registrarGuardado(guardado);

		// Si el pedido pertenece a una cuenta, recalcular el total de esa cuenta
		recalcularTotalCuentaSiAplica(guardado);
		return guardado;
	}

	@Override
	@Transactional
	public Pedido cambiarEstado(int id, String nuevoEstado) {
		Pedido existente = obtenerPorId(id);

		validarCuentaPermiteCambioEstado(existente, nuevoEstado);

		if (!existente.puedeTransicionarA(nuevoEstado)) {
			throw new BusinessException("TransiciÃ³n de estado no permitida para pedido #" + id + ": "
					+ existente.getEstado() + " â†’ " + nuevoEstado);
		}

		if (Pedido.ESTADO_EN_COCINA.equals(nuevoEstado)) {
			stockServicio.validarProductosActivos(existente.getDetalles());
		}

		if (Pedido.ESTADO_CANCELADO.equals(nuevoEstado)) {
			stockServicio.restaurar(existente.getDetalles());
		}

		Pedido guardado = repositorio.guardar(existente.conEstado(nuevoEstado));
		historialService.registrarCambioEstado(existente, guardado);

		// Si el pedido queda COMPLETADO o CANCELADO, verificar si la mesa
		// tiene otros pedidos activos; si no, marcarla como LIBRE (estado = true)
		if ((Pedido.ESTADO_COMPLETADO.equals(nuevoEstado) || Pedido.ESTADO_CANCELADO.equals(nuevoEstado))
				&& guardado.getFkMesa() != null) {
			int idMesa = guardado.getFkMesa().getIdmesa();
			boolean tieneOtrosPedidosActivos = repositorio.existePedidoActivoPorMesa(idMesa, guardado.getIdpedido());
			if (!tieneOtrosPedidosActivos) {
				mesaRepositorio.buscarPorId(idMesa).ifPresent(mesa ->
					mesaRepositorio.guardar(mesa.conEstado(true))
				);
			}
		}

		return guardado;
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
		historialService.registrarCambioEstado(pedido, cancelado);
		recalcularTotalCuentaSiAplica(cancelado);
	}

	// â”€â”€ Validaciones privadas â”€â”€

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
		// Solo cuentan para el total los pedidos que no estÃ¡n cancelados
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
					"Este pedido no se puede modificar porque estÃ¡ en estado " + pedido.getEstado());
		}
	}

	/**
	 * Evita modificar pedidos ligados a una cuenta ya pagada/anulada.
	 */
	private void validarCuentaNoCerrada(Pedido pedido) {
		if (pedido.getFkCuenta() != null && pedido.getFkCuenta().estaCerrada()) {
			throw new BusinessException("No se puede modificar este pedido porque su cuenta ya estÃ¡ "
					+ pedido.getFkCuenta().getEstado());
		}
	}
	private void validarCuentaPermiteCambioEstado(Pedido pedido, String nuevoEstado) {
		if (pedido.getFkCuenta() == null) {
			return;
		}
		String estadoCuenta = pedido.getFkCuenta().getEstado();
		if (Cuenta.ESTADO_ANULADA.equals(estadoCuenta)) {
			throw new BusinessException("No se puede modificar este pedido porque su cuenta ya esta " + estadoCuenta);
		}
		if (Cuenta.ESTADO_PAGADA.equals(estadoCuenta) && Pedido.ESTADO_CANCELADO.equals(nuevoEstado)) {
			throw new BusinessException("No se puede cancelar el pedido porque su cuenta ya esta " + estadoCuenta);
		}
	}

	private Cuenta resolverCuentaParaPedido(Pedido pedido) {
		if (pedido.getFkMesa() == null || pedido.getFkCliente() == null) {
			throw new BusinessException("El pedido debe tener mesa y cliente para vincularse a una cuenta.");
		}

		return cuentaRepositorio.buscarAbiertaPorMesa(pedido.getFkMesa().getIdmesa())
				.orElseGet(() -> cuentaRepositorio.guardar(new Cuenta(
						0,
						LocalDateTime.now(),
						null,
						Cuenta.ESTADO_ABIERTA,
						0.0,
						pedido.getFkMesa(),
						pedido.getFkCliente())));
	}

	private String normalizarEstadoDestino(String estadoDestino) {
		if (estadoDestino == null || estadoDestino.isBlank()) {
			return Pedido.ESTADO_PENDIENTE;
		}

		return estadoDestino.trim().toUpperCase();
	}
}


