package com.lachozag4.pisip.dominio.entidades;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Pago implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String METODO_EFECTIVO = "EFECTIVO";
	public static final String METODO_TARJETA = "TARJETA";
	public static final String METODO_TRANSFERENCIA = "TRANSFERENCIA";
	public static final String METODO_OTRO = "OTRO";

	private final int idpago;
	private final LocalDateTime fecha;
	private final double monto;
	private final String metodo;
	private final String referencia;
	private final String usuario;
	private final int idcuenta;
	private final int idcaja;

	public Pago(int idpago, LocalDateTime fecha, double monto, String metodo, String referencia, String usuario,
			int idcuenta, int idcaja) {
		this.idpago = idpago;
		this.fecha = fecha;
		this.monto = monto;
		this.metodo = metodo;
		this.referencia = referencia;
		this.usuario = usuario;
		this.idcuenta = idcuenta;
		this.idcaja = idcaja;
	}

	public int getIdpago() {
		return idpago;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public double getMonto() {
		return monto;
	}

	public String getMetodo() {
		return metodo;
	}

	public String getReferencia() {
		return referencia;
	}

	public String getUsuario() {
		return usuario;
	}

	public int getIdcuenta() {
		return idcuenta;
	}

	public int getIdcaja() {
		return idcaja;
	}
}
