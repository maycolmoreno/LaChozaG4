package com.lachozag4.pisip.aplicacion.casosuso.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lachozag4.pisip.aplicacion.excepciones.BusinessException;
import com.lachozag4.pisip.aplicacion.servicios.PedidoHistorialService;
import com.lachozag4.pisip.dominio.entidades.Categoria;
import com.lachozag4.pisip.dominio.entidades.Mesa;
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
    @Mock private PedidoHistorialService historialService;

    @InjectMocks private PedidoUseCaseImpl useCase;

    @Test
    void crearRechazaProductoInactivoAntesDeDescontarStockOGuardarPedido() {
        var categoria = new Categoria(1, "Bebidas", "Bar", true);
        var productoInactivo = new Producto(4, "Jugo", 2.50, 10, "", null, null, false, categoria);
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

    @Test
    void entregarPedidoNoLiberaMesaPorqueQuedaPendienteDeCobro() {
        var mesa = new Mesa(1, 5, 4, false, null);
        var pedidoListo = new Pedido(
                10,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                Pedido.ESTADO_LISTO_PARA_ENTREGA,
                null,
                null,
                mesa,
                null,
                null,
                List.of());

        when(repositorio.buscarPorId(10)).thenReturn(Optional.of(pedidoListo));
        when(repositorio.guardar(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var resultado = useCase.cambiarEstado(10, Pedido.ESTADO_COMPLETADO);

        assertThat(resultado.getEstado()).isEqualTo(Pedido.ESTADO_COMPLETADO);
        verify(mesaRepositorio, never()).buscarPorId(anyInt());
        verify(mesaRepositorio, never()).guardar(any());
    }
}
