package com.lachozag4.pisip.aplicacion.casosuso.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import com.lachozag4.pisip.dominio.entidades.Cliente;
import com.lachozag4.pisip.dominio.entidades.Cuenta;
import com.lachozag4.pisip.dominio.entidades.Mesa;
import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.dominio.repositorios.IClienteRepositorio;
import com.lachozag4.pisip.dominio.repositorios.ICuentaRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IMesaRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IPedidoRepositorio;

@ExtendWith(MockitoExtension.class)
class CuentaUseCaseImplTest {

    @Mock private ICuentaRepositorio repositorio;
    @Mock private IPedidoRepositorio pedidoRepositorio;
    @Mock private IClienteRepositorio clienteRepositorio;
    @Mock private IMesaRepositorio mesaRepositorio;

    @InjectMocks private CuentaUseCaseImpl useCase;

    @Test
    void crearRechazaSiMesaYaTieneCuentaAbierta() {
        Mesa mesa = new Mesa(3, 7, 4, false, 1);
        Cliente cliente = new Cliente(2, "Cliente", "123", "", "", "", true);
        Cuenta existente = new Cuenta(5, LocalDateTime.now(), null, Cuenta.ESTADO_ABIERTA, 0.0, mesa, cliente);
        Cuenta nueva = new Cuenta(0, LocalDateTime.now(), null, Cuenta.ESTADO_ABIERTA, 0.0, mesa, cliente);

        when(repositorio.buscarAbiertaPorMesa(3)).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> useCase.crear(nueva))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ya existe una cuenta abierta");

        verify(repositorio, never()).guardar(org.mockito.ArgumentMatchers.any(Cuenta.class));
    }

    @Test
    void agregarPedidoRechazaPedidoDeMesaDistinta() {
        Mesa mesaCuenta = new Mesa(3, 7, 4, false, 1);
        Mesa mesaPedido = new Mesa(4, 8, 4, false, 1);
        Cliente cliente = new Cliente(2, "Cliente", "123", "", "", "", true);
        Cuenta cuenta = new Cuenta(5, LocalDateTime.now(), null, Cuenta.ESTADO_ABIERTA, 0.0, mesaCuenta, cliente);
        Pedido pedido = new Pedido(9, LocalDateTime.now(), null, null, null, Pedido.ESTADO_PENDIENTE, null,
                null, mesaPedido, cliente, null, List.of());

        when(repositorio.buscarPorId(5)).thenReturn(Optional.of(cuenta));
        when(pedidoRepositorio.buscarPorId(9)).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> useCase.agregarPedido(5, 9))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("mesa distinta");

        verify(pedidoRepositorio, never()).actualizar(org.mockito.ArgumentMatchers.any(Pedido.class));
        verify(repositorio, never()).actualizar(org.mockito.ArgumentMatchers.any(Cuenta.class));
    }
}
