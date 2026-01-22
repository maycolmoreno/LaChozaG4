package com.lachozag4.pisip.dominio.entidades;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.ClienteJpa;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.MesaJpa;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.UsuarioJpa;

public class Pedido implements Serializable {

	private static final long serialVersionUID = 1L;

	private int idpedido;
	private LocalDateTime fecha;
	private boolean estado;
	private String observaciones;
	private UsuarioJpa FkUsuario;
	private MesaJpa Fkmesa;
	private ClienteJpa Fkcliente;
	public Pedido(int idpedido, LocalDateTime fecha, boolean estado, String observaciones, UsuarioJpa fkUsuario,
			MesaJpa fkmesa, ClienteJpa fkcliente) {
		
		this.idpedido = idpedido;
		this.fecha = fecha;
		this.estado = estado;
		this.observaciones = observaciones;
		FkUsuario = fkUsuario;
		Fkmesa = fkmesa;
		Fkcliente = fkcliente;
	}
	public int getIdpedido() {
		return idpedido;
	}
	public void setIdpedido(int idpedido) {
		this.idpedido = idpedido;
	}
	public LocalDateTime getFecha() {
		return fecha;
	}
	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}
	public boolean isEstado() {
		return estado;
	}
	public void setEstado(boolean estado) {
		this.estado = estado;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public UsuarioJpa getFkUsuario() {
		return FkUsuario;
	}
	public void setFkUsuario(UsuarioJpa fkUsuario) {
		FkUsuario = fkUsuario;
	}
	public MesaJpa getFkmesa() {
		return Fkmesa;
	}
	public void setFkmesa(MesaJpa fkmesa) {
		Fkmesa = fkmesa;
	}
	public ClienteJpa getFkcliente() {
		return Fkcliente;
	}
	public void setFkcliente(ClienteJpa fkcliente) {
		Fkcliente = fkcliente;
	}
	
	
}