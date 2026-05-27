package com.lachozag4.pisip.dominio.entidades;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Pedido implements Serializable {

	private static final long serialVersionUID = 1L;

	// Constantes de estado del pedido
	public static final String ESTADO_PENDIENTE = "PENDIENTE";
	public static final String ESTADO_EN_COCINA = "EN_COCINA";
	public static final String ESTADO_EN_BAR = "EN_BAR";
	public static final String ESTADO_LISTO_PARA_ENTREGA = "LISTO_PARA_ENTREGA";
	public static final String ESTADO_COMPLETADO = "COMPLETADO";
	public static final String ESTADO_CANCELADO = "CANCELADO";

	private static final java.util.Map<String, java.util.Set<String>> TRANSICIONES_PERMITIDAS = java.util.Map.of(
			ESTADO_PENDIENTE, java.util.Set.of(ESTADO_EN_COCINA, ESTADO_EN_BAR, ESTADO_CANCELADO),
			ESTADO_EN_COCINA, java.util.Set.of(ESTADO_LISTO_PARA_ENTREGA, ESTADO_CANCELADO),
			ESTADO_EN_BAR, java.util.Set.of(ESTADO_LISTO_PARA_ENTREGA, ESTADO_CANCELADO),
			ESTADO_LISTO_PARA_ENTREGA, java.util.Set.of(ESTADO_COMPLETADO, ESTADO_CANCELADO));

	private final int idpedido;
	private final LocalDateTime fecha;
	private final LocalDateTime fechaEnCocina;
	private final LocalDateTime fechaListoParaEntrega;
	private final LocalDateTime fechaEntregado;
	private final String estado;
	private final String observaciones;
	private Usuario fkUsuario;
	private Mesa fkMesa;
	private Cliente fkCliente;
	private Cuenta fkCuenta;
	private final List<PedidoDetalle> detalles;

	public Pedido(int idpedido, LocalDateTime fecha, LocalDateTime fechaEnCocina,
			LocalDateTime fechaListoParaEntrega, LocalDateTime fechaEntregado, String estado,
			String observaciones, Usuario fkUsuario, Mesa fkMesa, Cliente fkCliente, Cuenta fkCuenta,
			List<PedidoDetalle> detalles) {
		this.idpedido = idpedido;
		this.fecha = fecha;
		this.fechaEnCocina = fechaEnCocina;
		this.fechaListoParaEntrega = fechaListoParaEntrega;
		this.fechaEntregado = fechaEntregado;
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

	public LocalDateTime getFechaEnCocina() {
		return fechaEnCocina;
	}

	public LocalDateTime getFechaListoParaEntrega() {
		return fechaListoParaEntrega;
	}

	public LocalDateTime getFechaEntregado() {
		return fechaEntregado;
	}

	// ── Métodos de consulta de estado ──

	public boolean esPendiente() {
		return ESTADO_PENDIENTE.equals(estado);
	}

	public boolean estaEnCocina() {
		return ESTADO_EN_COCINA.equals(estado);
	}

	public boolean estaEnBar() {
		return ESTADO_EN_BAR.equals(estado);
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
		return esPendiente() || estaEnCocina() || estaEnBar() || esListoParaEntrega();
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

		LocalDateTime ahora = LocalDateTime.now();
		LocalDateTime nuevaFechaEnCocina = fechaEnCocina;
		LocalDateTime nuevaFechaListoParaEntrega = fechaListoParaEntrega;
		LocalDateTime nuevaFechaEntregado = fechaEntregado;

		if ((ESTADO_EN_COCINA.equals(nuevoEstado) || ESTADO_EN_BAR.equals(nuevoEstado)) && nuevaFechaEnCocina == null) {
			nuevaFechaEnCocina = ahora;
		}
		if (ESTADO_LISTO_PARA_ENTREGA.equals(nuevoEstado)) {
			nuevaFechaListoParaEntrega = ahora;
			if (nuevaFechaEnCocina == null) {
				nuevaFechaEnCocina = ahora;
			}
		}
		if (ESTADO_COMPLETADO.equals(nuevoEstado)) {
			nuevaFechaEntregado = ahora;
			if (nuevaFechaListoParaEntrega == null) {
				nuevaFechaListoParaEntrega = ahora;
			}
			if (nuevaFechaEnCocina == null) {
				nuevaFechaEnCocina = ahora;
			}
		}

		return new Pedido(idpedido, fecha, nuevaFechaEnCocina, nuevaFechaListoParaEntrega, nuevaFechaEntregado,
				nuevoEstado, observaciones, fkUsuario, fkMesa, fkCliente, fkCuenta, detalles);
	}

	/**
	 * Crea una copia con estado PENDIENTE (para creación).
	 */
	public Pedido comoPendiente() {
		return new Pedido(idpedido, fecha, fechaEnCocina, fechaListoParaEntrega, fechaEntregado, ESTADO_PENDIENTE,
				observaciones, fkUsuario, fkMesa, fkCliente, fkCuenta, detalles);
	}

	/**
	 * Crea una copia actualizando los datos del pedido pero manteniendo el estado
	 * actual.
	 */
	public Pedido conDatosActualizados(int id, LocalDateTime fecha, String observaciones, Usuario usuario, Mesa mesa,
			Cliente cliente, List<PedidoDetalle> detalles) {
		return new Pedido(id, fecha, fechaEnCocina, fechaListoParaEntrega, fechaEntregado, this.estado,
				observaciones, usuario, mesa, cliente, fkCuenta, detalles);
	}

	/**
	 * Crea una copia con el estado forzado, sin validar transiciones.
	 * Uso exclusivo al cerrar una cuenta (cajero finaliza el ciclo).
	 */
	public Pedido conEstadoForzado(String estadoForzado) {
		LocalDateTime ahora = LocalDateTime.now();
		LocalDateTime nuevaFechaEnCocina = fechaEnCocina;
		LocalDateTime nuevaFechaListoParaEntrega = fechaListoParaEntrega;
		LocalDateTime nuevaFechaEntregado = fechaEntregado;

		if ((ESTADO_EN_COCINA.equals(estadoForzado) || ESTADO_EN_BAR.equals(estadoForzado)) && nuevaFechaEnCocina == null) {
			nuevaFechaEnCocina = ahora;
		}
		if (ESTADO_LISTO_PARA_ENTREGA.equals(estadoForzado) && nuevaFechaListoParaEntrega == null) {
			nuevaFechaListoParaEntrega = ahora;
		}
		if (ESTADO_COMPLETADO.equals(estadoForzado) && nuevaFechaEntregado == null) {
			nuevaFechaEntregado = ahora;
		}

		return new Pedido(idpedido, fecha, nuevaFechaEnCocina, nuevaFechaListoParaEntrega, nuevaFechaEntregado,
				estadoForzado, observaciones, fkUsuario, fkMesa, fkCliente, fkCuenta, detalles);
	}

	/**
	 * Crea una copia asociando el pedido a la cuenta indicada.
	 */
	public Pedido conCuenta(Cuenta cuenta) {
		return new Pedido(idpedido, fecha, fechaEnCocina, fechaListoParaEntrega, fechaEntregado, estado,
				observaciones, fkUsuario, fkMesa, fkCliente, cuenta, detalles);
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
		return "Pedido{" + "idpedido=" + idpedido + ", fecha=" + fecha + ", fechaEnCocina=" + fechaEnCocina
				+ ", fechaListoParaEntrega=" + fechaListoParaEntrega + ", fechaEntregado=" + fechaEntregado
				+ ", estado=" + estado + ", observaciones='" + observaciones + '\'' + ", usuario=" + fkUsuario
				+ ", mesa=" + fkMesa + ", fkCliente=" + fkCliente + ", detalles=" + detalles + '}';
	}

}
