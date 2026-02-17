package com.lachozag4.pisip.aplicacion.casosuso.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.ICajaUseCase;
import com.lachozag4.pisip.aplicacion.excepciones.BusinessException;
import com.lachozag4.pisip.aplicacion.excepciones.NotFoundException;
import com.lachozag4.pisip.dominio.entidades.CajaTurno;
import com.lachozag4.pisip.dominio.repositorios.ICajaTurnoRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IPagoRepositorio;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CajaUseCaseImpl implements ICajaUseCase {

	private final ICajaTurnoRepositorio cajaRepositorio;
	private final IPagoRepositorio pagoRepositorio;

	@Override
	@Transactional
	public CajaTurno abrirCaja(double montoInicial, String usuarioApertura, String observaciones) {
		if (montoInicial < 0) {
			throw new BusinessException("El monto inicial no puede ser negativo");
		}
		if (usuarioApertura == null || usuarioApertura.isBlank()) {
			throw new BusinessException("El usuario de apertura es obligatorio");
		}

		cajaRepositorio.buscarCajaAbierta().ifPresent(caja -> {
			throw new BusinessException("Ya existe una caja abierta con ID: " + caja.getIdcaja());
		});

		CajaTurno caja = new CajaTurno(0, LocalDateTime.now(), null, montoInicial, null, null, null,
				CajaTurno.ESTADO_ABIERTA, usuarioApertura.trim(), null, observaciones);
		return cajaRepositorio.guardar(caja);
	}

	@Override
	@Transactional(readOnly = true)
	public CajaTurno obtenerCajaAbierta() {
		return cajaRepositorio.buscarCajaAbierta()
				.orElseThrow(() -> new NotFoundException("No hay una caja abierta actualmente"));
	}

	@Override
	@Transactional
	public CajaTurno cerrarCaja(double montoDeclaradoCierre, String usuarioCierre, String observaciones) {
		if (montoDeclaradoCierre < 0) {
			throw new BusinessException("El monto declarado de cierre no puede ser negativo");
		}
		if (usuarioCierre == null || usuarioCierre.isBlank()) {
			throw new BusinessException("El usuario de cierre es obligatorio");
		}

		CajaTurno cajaAbierta = obtenerCajaAbierta();
		double ingresos = pagoRepositorio.totalPagadoCaja(cajaAbierta.getIdcaja());
		double montoEsperado = cajaAbierta.getMontoInicial() + ingresos;

		CajaTurno cajaCerrada = cajaAbierta.conCierre(montoEsperado, montoDeclaradoCierre, usuarioCierre.trim(),
				observaciones, LocalDateTime.now());
		return cajaRepositorio.guardar(cajaCerrada);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CajaTurno> listar() {
		return cajaRepositorio.listarTodos();
	}
}
