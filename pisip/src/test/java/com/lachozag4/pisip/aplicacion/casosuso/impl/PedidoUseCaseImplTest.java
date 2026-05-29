package com.lachozag4.pisip.aplicacion.casosuso.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lachozag4.pisip.aplicacion.excepciones.BusinessException;
import com.lachozag4.pisip.dominio.entidades.Categoria;
import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.dominio.entidades.PedidoDetalle;
import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.dominio.repositorios.ICuentaRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IMesaRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IPedidoRepositorio;
import com.lachozag4.pisip.dominio.servicios.IGestionStockServicio;

@ExtendWith(MockitoExtension.class)
class PedidoUseCaseImplTest {

    @Mock private IPedidoRepositorio repositorio;
    @Mock private IGestionStockServicio stockServicio;
    @Mock private ICuentaRepositorio cuentaRepositorio;
    @Mock private IMesaRepositorio mesaRepositorio;

    @InjectMocks private PedidoUseCaseImpl useCase;

    @Test
    void crearRechazaProductoInactivoAntesDeDescontarStockOGuardarPedido() {
        var categoria = new Categoria(1, "Bebidas", "Bar", true);
        var productoInactivo = new Producto(4, "Jugo", 2.50, 10, "", null, false, categoria);
        var detalle = new PedidoDetalle(0, productoInactivo, null, 1, productoInactivo.getPrecio());
        var pedido = new Pedido(0, LocalDateTime.now(), null, null, null, null, null, null, null, null, null,
                List.of(detalle));

        doThrow(new BusinessException("El producto 'Jugo' ya no esta disponible"))
                .when(stockServicio).validarProductosActivos(pedido.getDetalles());

        assertThatThrownBy(() -> useCase.crear(pedido))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Jugo");

        verify(stockServicio).validarProductosActivos(pedido.getDetalles());
        verify(stockServicio, never()).validarYDescontar(pedido.getDetalles());
        verify(repositorio, never()).guardar(pedido);
    }
}
