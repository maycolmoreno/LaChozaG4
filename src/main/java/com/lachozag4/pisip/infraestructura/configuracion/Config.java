package com.lachozag4.pisip.infraestructura.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.CrearPedidoPort;
import com.lachozag4.pisip.aplicacion.casosuso.impl.CrearPedidoUseCase;
import com.lachozag4.pisip.aplicacion.puertos.salida.PedidoRepositoryPort;

@Configuration
public class Config {

    @Bean
    public CrearPedidoPort crearPedidoUseCase(PedidoRepositoryPort pedidoRepo) {
        return new CrearPedidoUseCase(pedidoRepo);
    }
}
