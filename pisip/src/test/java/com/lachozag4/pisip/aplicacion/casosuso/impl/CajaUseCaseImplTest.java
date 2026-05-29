package com.lachozag4.pisip.aplicacion.casosuso.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
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
import com.lachozag4.pisip.dominio.entidades.CajaTurno;
import com.lachozag4.pisip.dominio.repositorios.ICajaTurnoRepositorio;
import com.lachozag4.pisip.dominio.repositorios.IPagoRepositorio;

@ExtendWith(MockitoExtension.class)
class CajaUseCaseImplTest {

    @Mock private ICajaTurnoRepositorio cajaRepositorio;
    @Mock private IPagoRepositorio pagoRepositorio;

    @InjectMocks private CajaUseCaseImpl useCase;

    @Test
    void abrirCajaRechazaSiYaExisteCajaAbierta() {
        var abierta = new CajaTurno(8, LocalDateTime.now(), null, 20.0, null, null, null,
                CajaTurno.ESTADO_ABIERTA, "cajero", null, null);

        when(cajaRepositorio.buscarCajaAbierta()).thenReturn(Optional.of(abierta));

        assertThatThrownBy(() -> useCase.abrirCaja(10.0, "otro-cajero", null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Ya existe una caja abierta");

        verify(cajaRepositorio, never()).guardar(org.mockito.ArgumentMatchers.any(CajaTurno.class));
    }
}
