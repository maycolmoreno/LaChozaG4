package com.lachozag4.pisip.dominio.entidades;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Cuenta implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String ESTADO_ABIERTA = "ABIERTA";
	public static final String ESTADO_PAGADA = "PAGADA";
	public static final String ESTADO_ANULADA = "ANULADA";

	private final int idcuenta;
	private final LocalDateTime fechaApertura;
	private final LocalDateTime fechaCierre;
	private final String estado;
	private final double total;
	private Mesa fkMesa;
	private Cliente fkCliente;

	public Cuenta(int idcuenta, LocalDateTime fechaApertura, LocalDateTime fechaCierre, String estado, double total,
			Mesa fkMesa, Cliente fkCliente) {
		this.idcuenta = idcuenta;
		this.fechaApertura = fechaApertura;
		this.fechaCierre = fechaCierre;
		this.estado = estado;
		this.total = total;
		this.fkMesa = fkMesa;
		this.fkCliente = fkCliente;
	}

	public int getIdcuenta() {
		return idcuenta;
	}

	public LocalDateTime getFechaApertura() {
		return fechaApertura;
	}

	public LocalDateTime getFechaCierre() {
		return fechaCierre;
	}

	public String getEstado() {
		return estado;
	}

	public double getTotal() {
		return total;
	}

	public Mesa getFkMesa() {
		return fkMesa;
	}

	public void setFkMesa(Mesa fkMesa) {
		this.fkMesa = fkMesa;
	}

	public Cliente getFkCliente() {
		return fkCliente;
	}

	public void setFkCliente(Cliente fkCliente) {
		this.fkCliente = fkCliente;
	}

	public boolean estaAbierta() {
		return ESTADO_ABIERTA.equals(estado);
	}

	public boolean estaCerrada() {
		return ESTADO_PAGADA.equals(estado) || ESTADO_ANULADA.equals(estado);
	}

	public Cuenta comoAbierta() {
		return new Cuenta(idcuenta, fechaApertura, null, ESTADO_ABIERTA, total, fkMesa, fkCliente);
	}

	public Cuenta conEstado(String nuevoEstado, LocalDateTime nuevaFechaCierre) {
		return new Cuenta(idcuenta, fechaApertura, nuevaFechaCierre, nuevoEstado, total, fkMesa, fkCliente);
	}

	public Cuenta conTotal(double nuevoTotal) {
		return new Cuenta(idcuenta, fechaApertura, fechaCierre, estado, nuevoTotal, fkMesa, fkCliente);
	}

	@Override
	public String toString() {
		return "Cuenta{" + "idcuenta=" + idcuenta + ", fechaApertura=" + fechaApertura + ", fechaCierre="
				+ fechaCierre + ", estado='" + estado + '\'' + ", total=" + total + ", mesa=" + fkMesa + ", cliente="
				+ fkCliente + '}';
	}
}
