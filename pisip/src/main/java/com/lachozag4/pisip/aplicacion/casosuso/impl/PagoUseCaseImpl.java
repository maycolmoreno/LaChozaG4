package com.lachozag4.pisip.aplicacion.casosuso.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IPagoUseCase;
import com.lachozag4.pisip.aplicacion.excepciones.BusinessException;
import com.lachozag4.pisip.aplicacion.excepciones.NotFoundException;
import com.lachozag4.pisip.aplicacion.servicios.ComprobanteService;
import com.lachozag4.pisip.dominio.entidades.CajaTurno;
import com.lachozag4.pisip.dominio.entidades.Cuenta;
import com.lachozag4.pisip.dominio.entidades.Mesa;
import com.lachozag4.pisip.dominio.entidades.Pago;
import com.lachozag4.pisip.dominio.repositorios.ICajaTurnoRepositorio;
import com.lachozag4.pisip.dominio.repositorios.ICuentaRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IMesaRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IPagoRepositorio;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PagoUseCaseImpl implements IPagoUseCase {

	private static final Set<String> METODOS_VALIDOS = Set.of(Pago.METODO_EFECTIVO, Pago.METODO_TARJETA,
			Pago.METODO_TRANSFERENCIA, Pago.METODO_OTRO);
	private static final double EPSILON = 0.0001;

	private final IPagoRepositorio pagoRepositorio;
	private final ICuentaRepositorio cuentaRepositorio;
	private final ICajaTurnoRepositorio cajaRepositorio;
	private final IMesaRepositorio mesaRepositorio;
	private final ComprobanteService comprobanteService;

	@Override
	@Transactional
	public Pago registrarPago(int idcuenta, double monto, String metodo, String referencia, String usuario) {
		if (monto <= 0) {
			throw new BusinessException("El monto del pago debe ser mayor a 0");
		}
		if (metodo == null || metodo.isBlank()) {
			throw new BusinessException("El método de pago es obligatorio");
		}
		String metodoNormalizado = metodo.trim().toUpperCase();
		if (!METODOS_VALIDOS.contains(metodoNormalizado)) {
			throw new BusinessException("Método de pago no válido: " + metodo);
		}
		if (usuario == null || usuario.isBlank()) {
			throw new BusinessException("El usuario que registra el pago es obligatorio");
		}

		Cuenta cuenta = cuentaRepositorio.buscarPorIdParaActualizar(idcuenta)
				.orElseThrow(() -> new NotFoundException("Cuenta no encontrada con ID: " + idcuenta));
		if (!cuenta.estaAbierta()) {
			throw new BusinessException("Solo se pueden registrar pagos en cuentas abiertas");
		}

		CajaTurno cajaAbierta = cajaRepositorio.buscarCajaAbierta()
				.orElseThrow(() -> new BusinessException("No hay una caja abierta para registrar el pago"));

		double totalPagado = pagoRepositorio.totalPagadoCuenta(idcuenta);
		double saldoPendiente = cuenta.getTotal() - totalPagado;
		if (saldoPendiente <= EPSILON) {
			throw new BusinessException("La cuenta ya se encuentra completamente pagada");
		}
		if (monto - saldoPendiente > EPSILON) {
			throw new BusinessException("El pago excede el saldo pendiente de la cuenta");
		}

		Pago pago = new Pago(0, LocalDateTime.now(), monto, metodoNormalizado, referencia, usuario.trim(), idcuenta,
				cajaAbierta.getIdcaja());
		Pago guardado = pagoRepositorio.guardar(pago);

		double nuevoTotalPagado = totalPagado + monto;
		if (nuevoTotalPagado + EPSILON >= cuenta.getTotal()) {
			Cuenta pagada = cuenta.conEstado(Cuenta.ESTADO_PAGADA, LocalDateTime.now());
			cuentaRepositorio.actualizar(pagada);
			Mesa mesa = cuenta.getFkMesa();
			if (mesa != null) {
				mesaRepositorio.actualizar(mesa.conEstado(true));
			}
		}

		return guardado;
	}

	@Override
	@Transactional
	public Pago registrarPagoConComprobante(int idcuenta, double monto, String metodo, String referencia,
			String usuario, MultipartFile archivo) {
		Pago pago = registrarPago(idcuenta, monto, metodo, referencia, usuario);
		comprobanteService.subirComprobante(pago.getIdpago(), archivo, usuario);
		return pago;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Pago> listarPorCuenta(int idcuenta) {
		return pagoRepositorio.listarPorCuenta(idcuenta);
	}

	@Override
	@Transactional(readOnly = true)
	public double totalPagadoCuenta(int idcuenta) {
		return pagoRepositorio.totalPagadoCuenta(idcuenta);
	}

	@Override
	@Transactional(readOnly = true)
	public double saldoPendienteCuenta(int idcuenta) {
		Cuenta cuenta = cuentaRepositorio.buscarPorId(idcuenta)
				.orElseThrow(() -> new NotFoundException("Cuenta no encontrada con ID: " + idcuenta));
		double saldo = cuenta.getTotal() - pagoRepositorio.totalPagadoCuenta(idcuenta);
		return saldo > 0 ? saldo : 0.0;
	}
}
