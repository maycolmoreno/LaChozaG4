package com.lachozag4.pisip.aplicacion.casosuso.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.ICuentaUseCase;
import com.lachozag4.pisip.aplicacion.excepciones.BusinessException;
import com.lachozag4.pisip.aplicacion.excepciones.NotFoundException;
import com.lachozag4.pisip.dominio.entidades.Cuenta;
import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.dominio.repositorios.ICuentaRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IPedidoRepositorio;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CuentaUseCaseImpl implements ICuentaUseCase {

	private final ICuentaRepositorio repositorio;
	private final IPedidoRepositorio pedidoRepositorio;

	@Override
	@Transactional
	public Cuenta crear(Cuenta cuenta) {
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

		// Antes de cobrar o anular, validar que no haya pedidos abiertos
		if (Cuenta.ESTADO_PAGADA.equals(nuevoEstado) || Cuenta.ESTADO_ANULADA.equals(nuevoEstado)) {
			boolean hayPedidosNoFinalizados = pedidoRepositorio.listarPorCuenta(idcuenta).stream()
					.anyMatch(p -> !p.esEstadoFinal());
			if (hayPedidosNoFinalizados) {
				throw new BusinessException(
					"No se puede cerrar la cuenta porque tiene pedidos que aún no están completados o cancelados");
			}
		}

		if (!Cuenta.ESTADO_PAGADA.equals(nuevoEstado) && !Cuenta.ESTADO_ANULADA.equals(nuevoEstado)) {
			throw new BusinessException("Estado de cuenta no válido: " + nuevoEstado);
		}

		LocalDateTime ahora = LocalDateTime.now();
		Cuenta actualizada = existente.conEstado(nuevoEstado, ahora);
		return repositorio.actualizar(actualizada);
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

		// Asociar el pedido a la cuenta
		pedido.setFkCuenta(cuenta);
		pedidoRepositorio.actualizar(pedido);

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
