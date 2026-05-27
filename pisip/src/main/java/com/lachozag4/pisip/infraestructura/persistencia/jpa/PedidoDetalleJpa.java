package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import java.io.Serializable;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pedido_detalle")
@Getter
@Setter
@NoArgsConstructor
public class PedidoDetalleJpa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idpedidodetalle;

	@ManyToOne
	@JoinColumn(name="fkPedido")
    private PedidoJpa fkPedido; 
    
	@ManyToOne
	@JoinColumn(name="fkProducto")
    private ProductoJpa fkProducto;
    
    @Column(nullable = false)
    private int cantidad;

    @Column(nullable = false)
    private double precioUnitario;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PedidoDetalleJpa that)) return false;
		return idpedidodetalle != 0 && idpedidodetalle == that.idpedidodetalle;
	}

	@Override
	public int hashCode() { return getClass().hashCode(); }

	@Override
	public String toString() { return "PedidoDetalleJpa(id=" + idpedidodetalle + ")"; }
}
