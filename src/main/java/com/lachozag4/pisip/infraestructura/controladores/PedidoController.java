package com.lachozag4.pisip.infraestructura.controladores;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.CrearPedidoPort;
import com.lachozag4.pisip.dominio.entidades.Pedido;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final CrearPedidoPort crearPedidoPort;

    public PedidoController(CrearPedidoPort crearPedidoPort) {
        this.crearPedidoPort = crearPedidoPort;
    }

    @PostMapping
    public Pedido crearPedido(@RequestBody Pedido pedido) {
        return crearPedidoPort.ejecutar(pedido);
    }
}
