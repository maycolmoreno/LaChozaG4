package com.lachozag4.pisip.dominio.entidades;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CajaTurno implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String ESTADO_ABIERTA = "ABIERTA";
	public static final String ESTADO_CERRADA = "CERRADA";

	private final int idcaja;
	private final LocalDateTime fechaApertura;
	private final LocalDateTime fechaCierre;
	private final double montoInicial;
	private final Double montoEsperadoCierre;
	private final Double montoDeclaradoCierre;
	private final Double diferencia;
	private final String estado;
	private final String usuarioApertura;
	private final String usuarioCierre;
	private final String observaciones;

	public CajaTurno(int idcaja, LocalDateTime fechaApertura, LocalDateTime fechaCierre, double montoInicial,
			Double montoEsperadoCierre, Double montoDeclaradoCierre, Double diferencia, String estado,
			String usuarioApertura, String usuarioCierre, String observaciones) {
		this.idcaja = idcaja;
		this.fechaApertura = fechaApertura;
		this.fechaCierre = fechaCierre;
		this.montoInicial = montoInicial;
		this.montoEsperadoCierre = montoEsperadoCierre;
		this.montoDeclaradoCierre = montoDeclaradoCierre;
		this.diferencia = diferencia;
		this.estado = estado;
		this.usuarioApertura = usuarioApertura;
		this.usuarioCierre = usuarioCierre;
		this.observaciones = observaciones;
	}

	public int getIdcaja() {
		return idcaja;
	}

	public LocalDateTime getFechaApertura() {
		return fechaApertura;
	}

	public LocalDateTime getFechaCierre() {
		return fechaCierre;
	}

	public double getMontoInicial() {
		return montoInicial;
	}

	public Double getMontoEsperadoCierre() {
		return montoEsperadoCierre;
	}

	public Double getMontoDeclaradoCierre() {
		return montoDeclaradoCierre;
	}

	public Double getDiferencia() {
		return diferencia;
	}

	public String getEstado() {
		return estado;
	}

	public String getUsuarioApertura() {
		return usuarioApertura;
	}

	public String getUsuarioCierre() {
		return usuarioCierre;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public boolean estaAbierta() {
		return ESTADO_ABIERTA.equals(estado);
	}

	public CajaTurno conCierre(double montoEsperado, double montoDeclarado, String usuarioCierre,
			String observacionesCierre, LocalDateTime fechaCierre) {
		double diferenciaCierre = montoDeclarado - montoEsperado;
		return new CajaTurno(idcaja, fechaApertura, fechaCierre, montoInicial, montoEsperado, montoDeclarado,
				diferenciaCierre, ESTADO_CERRADA, usuarioApertura, usuarioCierre, observacionesCierre);
	}
}
