package com.lachozag4.pisip.aplicacion.casosuso.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.ICuentaUseCase;
import com.lachozag4.pisip.aplicacion.excepciones.BusinessException;
import com.lachozag4.pisip.aplicacion.excepciones.NotFoundException;
import com.lachozag4.pisip.aplicacion.servicios.PedidoHistorialService;
import com.lachozag4.pisip.dominio.entidades.Cuenta;
import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.dominio.repositorios.IClienteRepositorio;
import com.lachozag4.pisip.dominio.repositorios.ICuentaRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IMesaRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IPedidoRepositorio;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CuentaUseCaseImpl implements ICuentaUseCase {

	private final ICuentaRepositorio repositorio;
	private final IPedidoRepositorio pedidoRepositorio;
	private final IClienteRepositorio clienteRepositorio;
	private final IMesaRepositorio mesaRepositorio;
	private final PedidoHistorialService historialService;

	@Override
	@Transactional
	public Cuenta crear(Cuenta cuenta) {
		if (cuenta.getFkMesa() != null) {
			repositorio.buscarAbiertaPorMesa(cuenta.getFkMesa().getIdmesa()).ifPresent(abierta -> {
				throw new BusinessException("Ya existe una cuenta abierta para esta mesa.");
			});
		}

		// Siempre se crea como ABIERTA con fecha de apertura ahora si no viene seteada
		Cuenta nueva = new Cuenta(
			0,
			cuenta.getFechaApertura() != null ? cuenta.getFechaApertura() : LocalDateTime.now(),
			null,
			Cuenta.ESTADO_ABIERTA,
			cuenta.getTotal(),
			cuenta.getFkMesa(),
			cuenta.getFkCliente());
		return repositorio.guardar(nueva);
	}

	@Override
	public Cuenta obtenerPorId(int idcuenta) {
		return repositorio.buscarPorId(idcuenta)
				.orElseThrow(() -> new NotFoundException("Cuenta no encontrada con ID: " + idcuenta));
	}

	@Override
	public Cuenta obtenerAbiertaPorMesa(int idMesa) {
		return repositorio.buscarAbiertaPorMesa(idMesa)
				.orElseThrow(() -> new NotFoundException("No existe cuenta abierta para la mesa con ID: " + idMesa));
	}

	@Override
	public List<Cuenta> listar() {
		return repositorio.listarTodas();
	}

	@Override
	public List<Cuenta> listarAbiertas() {
		return repositorio.listarAbiertas();
	}

	@Override
	@Transactional
	public Cuenta cambiarEstado(int idcuenta, String nuevoEstado) {
		Cuenta existente = obtenerPorId(idcuenta);

		if (existente.estaCerrada()) {
			throw new BusinessException("La cuenta ya está cerrada con estado " + existente.getEstado());
		}

		if (!Cuenta.ESTADO_PAGADA.equals(nuevoEstado) && !Cuenta.ESTADO_ANULADA.equals(nuevoEstado)) {
			throw new BusinessException("Estado de cuenta no válido: " + nuevoEstado);
		}

		// Al pagar, marcar como COMPLETADO todos los pedidos activos de la cuenta
		if (Cuenta.ESTADO_PAGADA.equals(nuevoEstado)) {
			pedidoRepositorio.listarPorCuenta(idcuenta).stream()
					.filter(p -> !p.esEstadoFinal())
					.forEach(p -> {
						var actualizado = pedidoRepositorio.actualizar(p.conEstadoForzado(Pedido.ESTADO_COMPLETADO));
						historialService.registrarCambioEstado(p, actualizado);
					});
		}

		LocalDateTime ahora = LocalDateTime.now();
		Cuenta actualizada = existente.conEstado(nuevoEstado, ahora);
		Cuenta resultado = repositorio.actualizar(actualizada);

		// Liberar la mesa al cerrar la cuenta (PAGADA o ANULADA)
		if (existente.getFkMesa() != null) {
			mesaRepositorio.buscarPorId(existente.getFkMesa().getIdmesa())
					.ifPresent(mesa -> mesaRepositorio.actualizar(mesa.conEstado(true)));
			pedidoRepositorio.listarPorCuenta(idcuenta).forEach(pedido ->
					historialService.registrarEvento(
							pedido.getIdpedido(),
							PedidoHistorialService.ACCION_CERRAR_MESA,
							pedido.getEstado(),
							pedido.getEstado(),
							"Mesa liberada por cierre de cuenta " + nuevoEstado));
		}

		return resultado;
	}

	@Override
	@Transactional
	public Cuenta asignarCliente(int idcuenta, int idCliente) {
		Cuenta cuenta = obtenerPorId(idcuenta);
		if (cuenta.estaCerrada()) {
			throw new BusinessException("No se puede cambiar el cliente de una cuenta cerrada");
		}
		var cliente = clienteRepositorio.buscarPorId(idCliente)
				.orElseThrow(() -> new NotFoundException("Cliente no encontrado con ID: " + idCliente));
		return repositorio.actualizar(cuenta.conCliente(cliente));
	}

	@Override
	@Transactional
	public void eliminar(int idcuenta) {
		Cuenta existente = obtenerPorId(idcuenta);
		if (!existente.estaAbierta()) {
			throw new BusinessException("Solo se pueden eliminar cuentas abiertas");
		}
		repositorio.eliminar(idcuenta);
	}

	@Override
	@Transactional
	public Cuenta agregarPedido(int idcuenta, int idpedido) {
		Cuenta cuenta = obtenerPorId(idcuenta);
		if (!cuenta.estaAbierta()) {
			throw new BusinessException("Solo se pueden agregar pedidos a cuentas abiertas");
		}

		var pedido = pedidoRepositorio.buscarPorId(idpedido)
				.orElseThrow(() -> new NotFoundException("Pedido no encontrado con ID: " + idpedido));

		if (cuenta.getFkMesa() != null && pedido.getFkMesa() != null
				&& cuenta.getFkMesa().getIdmesa() != pedido.getFkMesa().getIdmesa()) {
			throw new BusinessException("El pedido pertenece a una mesa distinta a la cuenta.");
		}

		// Asociar el pedido a la cuenta
		pedidoRepositorio.actualizar(pedido.conCuenta(cuenta));

		// Recalcular total de la cuenta como suma de todos los pedidos asociados
		// ignorando los que ya están cancelados
		double total = pedidoRepositorio.listarPorCuenta(idcuenta).stream()
				.filter(p -> !Pedido.ESTADO_CANCELADO.equals(p.getEstado()))
				.flatMap(p -> p.getDetalles().stream())
				.mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
				.sum();

		Cuenta actualizada = cuenta.conTotal(total);
		return repositorio.actualizar(actualizada);
	}
}
