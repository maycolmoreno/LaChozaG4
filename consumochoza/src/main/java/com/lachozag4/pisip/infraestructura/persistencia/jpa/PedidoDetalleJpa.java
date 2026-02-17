package com.lachozag4.pisip.infraestructura.persistencia.jpa;

import java.io.Serializable;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pedido_detalle")
@Data
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

}
