package com.lachozag4.pisip.aplicacion.casosuso.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lachozag4.pisip.aplicacion.excepciones.BusinessException;
import com.lachozag4.pisip.aplicacion.servicios.ComprobanteService;
import com.lachozag4.pisip.dominio.entidades.CajaTurno;
import com.lachozag4.pisip.dominio.entidades.Cuenta;
import com.lachozag4.pisip.dominio.repositorios.ICajaTurnoRepositorio;
import com.lachozag4.pisip.dominio.repositorios.ICuentaRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IMesaRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IPagoRepositorio;

@ExtendWith(MockitoExtension.class)
class PagoUseCaseImplTest {

    @Mock private IPagoRepositorio pagoRepositorio;
    @Mock private ICuentaRepositorio cuentaRepositorio;
    @Mock private ICajaTurnoRepositorio cajaRepositorio;
    @Mock private IMesaRepositorio mesaRepositorio;
    @Mock private ComprobanteService comprobanteService;

    @InjectMocks private PagoUseCaseImpl useCase;

    @Test
    void registrarPagoBuscaCuentaConBloqueoParaEvitarDobleCobro() {
        var cuenta = new Cuenta(10, LocalDateTime.now(), null, Cuenta.ESTADO_ABIERTA, 20.0, null, null);
        var caja = new CajaTurno(5, LocalDateTime.now(), null, 0, null, null, null,
                CajaTurno.ESTADO_ABIERTA, "cajero", null, null);

        when(cuentaRepositorio.buscarPorIdParaActualizar(10)).thenReturn(Optional.of(cuenta));
        when(cajaRepositorio.buscarCajaAbierta()).thenReturn(Optional.of(caja));
        when(pagoRepositorio.totalPagadoCuenta(10)).thenReturn(0.0);

        useCase.registrarPago(10, 10.0, "EFECTIVO", null, "cajero");

        verify(cuentaRepositorio).buscarPorIdParaActualizar(10);
    }

    @Test
    void registrarPagoRechazaMontoMayorAlSaldoPendiente() {
        var cuenta = new Cuenta(10, LocalDateTime.now(), null, Cuenta.ESTADO_ABIERTA, 20.0, null, null);
        var caja = new CajaTurno(5, LocalDateTime.now(), null, 0, null, null, null,
                CajaTurno.ESTADO_ABIERTA, "cajero", null, null);

        when(cuentaRepositorio.buscarPorIdParaActualizar(10)).thenReturn(Optional.of(cuenta));
        when(cajaRepositorio.buscarCajaAbierta()).thenReturn(Optional.of(caja));
        when(pagoRepositorio.totalPagadoCuenta(10)).thenReturn(15.0);

        assertThatThrownBy(() -> useCase.registrarPago(10, 10.0, "EFECTIVO", null, "cajero"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("excede");
    }
}
