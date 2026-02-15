package com.lachozag4.pisip.dominio.entidades;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Pedido implements Serializable {

	private static final long serialVersionUID = 1L;

	// Constantes de estado del pedido
	public static final String ESTADO_PENDIENTE = "PENDIENTE";
	public static final String ESTADO_EN_COCINA = "EN_COCINA";
	public static final String ESTADO_LISTO_PARA_ENTREGA = "LISTO_PARA_ENTREGA";
	public static final String ESTADO_COMPLETADO = "COMPLETADO";
	public static final String ESTADO_CANCELADO = "CANCELADO";

	private static final java.util.Map<String, java.util.Set<String>> TRANSICIONES_PERMITIDAS = java.util.Map.of(
			ESTADO_PENDIENTE, java.util.Set.of(ESTADO_EN_COCINA, ESTADO_CANCELADO),
			ESTADO_EN_COCINA, java.util.Set.of(ESTADO_LISTO_PARA_ENTREGA, ESTADO_CANCELADO),
			ESTADO_LISTO_PARA_ENTREGA, java.util.Set.of(ESTADO_COMPLETADO, ESTADO_CANCELADO));

	private final int idpedido;
	private final LocalDateTime fecha;
	private final String estado;
	private final String observaciones;
	private Usuario fkUsuario;
	private Mesa fkMesa;
	private Cliente fkCliente;
	private Cuenta fkCuenta;
	private final List<PedidoDetalle> detalles;

	public Pedido(int idpedido, LocalDateTime fecha, String estado, String observaciones, Usuario fkUsuario,
			Mesa fkMesa, Cliente fkCliente, Cuenta fkCuenta, List<PedidoDetalle> detalles) {
		this.idpedido = idpedido;
		this.fecha = fecha;
		this.estado = estado;
		this.observaciones = observaciones;
		this.fkUsuario = fkUsuario;
		this.fkMesa = fkMesa;
		this.fkCliente = fkCliente;
		this.fkCuenta = fkCuenta;
		this.detalles = detalles;
	}

	public int getIdpedido() {
		return idpedido;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public String getEstado() {
		return estado;
	}

	// ── Métodos de consulta de estado ──

	public boolean esPendiente() {
		return ESTADO_PENDIENTE.equals(estado);
	}

	public boolean estaEnCocina() {
		return ESTADO_EN_COCINA.equals(estado);
	}

	public boolean esListoParaEntrega() {
		return ESTADO_LISTO_PARA_ENTREGA.equals(estado);
	}

	public boolean esCompletado() {
		return ESTADO_COMPLETADO.equals(estado);
	}

	public boolean esCancelado() {
		return ESTADO_CANCELADO.equals(estado);
	}

	public boolean esEstadoFinal() {
		return esCompletado() || esCancelado();
	}

	public boolean esActivo() {
		return esPendiente() || estaEnCocina() || esListoParaEntrega();
	}

	public boolean esEditable() {
		return esPendiente();
	}

	// ── Reglas de negocio de transición de estado ──

	/**
	 * Verifica si la transición de estado es válida según las reglas del dominio.
	 * PENDIENTE → EN_COCINA | CANCELADO
	 * EN_COCINA → LISTO_PARA_ENTREGA | CANCELADO
	 * LISTO_PARA_ENTREGA → COMPLETADO | CANCELADO
	 */
	public boolean puedeTransicionarA(String nuevoEstado) {
		if (estado == null || nuevoEstado == null)
			return false;
		var destinos = TRANSICIONES_PERMITIDAS.get(estado);
		return destinos != null && destinos.contains(nuevoEstado);
	}

	/**
	 * Crea una nueva instancia del pedido con el estado cambiado (inmutabilidad).
	 * Lanza IllegalStateException si la transición no es válida.
	 */
	public Pedido conEstado(String nuevoEstado) {
		if (!puedeTransicionarA(nuevoEstado)) {
			throw new IllegalStateException("Transición de estado no permitida: " + estado + " → " + nuevoEstado);
		}
		return new Pedido(idpedido, fecha, nuevoEstado, observaciones, fkUsuario, fkMesa, fkCliente, fkCuenta,
				detalles);
	}

	/**
	 * Crea una copia con estado PENDIENTE (para creación).
	 */
	public Pedido comoPendiente() {
		return new Pedido(idpedido, fecha, ESTADO_PENDIENTE, observaciones, fkUsuario, fkMesa, fkCliente, fkCuenta,
				detalles);
	}

	/**
	 * Crea una copia actualizando los datos del pedido pero manteniendo el estado
	 * actual.
	 */
	public Pedido conDatosActualizados(int id, LocalDateTime fecha, String observaciones, Usuario usuario, Mesa mesa,
			Cliente cliente, List<PedidoDetalle> detalles) {
		return new Pedido(id, fecha, this.estado, observaciones, usuario, mesa, cliente, fkCuenta, detalles);
	}

	public String getObservaciones() {
		return observaciones;
	}

	public Usuario getFkUsuario() {
		return fkUsuario;
	}

	public void setFkUsuario(Usuario fkUsuario) {
		this.fkUsuario = fkUsuario;
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

	public Cuenta getFkCuenta() {
		return fkCuenta;
	}

	public void setFkCuenta(Cuenta fkCuenta) {
		this.fkCuenta = fkCuenta;
	}

	public List<PedidoDetalle> getDetalles() {
		return detalles;
	}

	@Override
	public String toString() {
		return "Pedido{" + "idpedido=" + idpedido + ", fecha=" + fecha + ", estado=" + estado + ", observaciones='"
				+ observaciones + '\'' + ", usuario=" + fkUsuario + ", mesa=" + fkMesa + ", fkCliente=" + fkCliente
				+ ", detalles=" + detalles + '}';
	}

}