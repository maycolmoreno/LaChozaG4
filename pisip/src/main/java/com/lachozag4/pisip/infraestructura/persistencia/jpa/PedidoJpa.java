package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.lachozag4.pisip.dominio.enums.EstadoPedido;

@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
public class PedidoJpa implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idpedido;

	@Column(nullable = false)
	private LocalDateTime fecha;

	@Column(name = "fecha_en_cocina")
	private LocalDateTime fechaEnCocina;

	@Column(name = "fecha_listo_para_entrega")
	private LocalDateTime fechaListoParaEntrega;

	@Column(name = "fecha_entregado")
	private LocalDateTime fechaEntregado;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private EstadoPedido estado;

	@Column(length = 255)
	private String observaciones;

	// ✅ RELACIÓN: Muchos pedidos pertenecen a un usuario
	@ManyToOne
	@JoinColumn(name = "fkUsuario")
	private UsuarioJpa fkUsuario;

	// ✅ RELACIÓN: Muchos pedidos pertenecen a una mesa
	@ManyToOne
	@JoinColumn(name="fkMesa")
	private MesaJpa fkMesa;

	// ✅ RELACIÓN: Muchos pedidos pertenecen a un cliente
	@ManyToOne
	@JoinColumn(name="fkCliente")
	private ClienteJpa fkCliente;

	// ✅ RELACIÓN: Muchos pedidos pertenecen a una cuenta (opcional)
	@ManyToOne
	@JoinColumn(name="fkCuenta")
	private CuentaJpa fkCuenta;

	// ✅ RELACIÓN: Un pedido tiene muchos detalles
	@OneToMany(mappedBy = "fkPedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<PedidoDetalleJpa> detalles = new ArrayList<>();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PedidoJpa that)) return false;
		return idpedido != 0 && idpedido == that.idpedido;
	}

	@Override
	public int hashCode() { return getClass().hashCode(); }

	@Override
	public String toString() { return "PedidoJpa(id=" + idpedido + ")"; }
}