package com.lachozag4.pisip.aplicacion.casosuso.impl;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.ConsultarMesasPort;
import com.lachozag4.pisip.aplicacion.puertos.salida.MesaRepositoryPort;
import com.lachozag4.pisip.dominio.entidades.Mesa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultarMesasUseCase implements ConsultarMesasPort {

    private final MesaRepositoryPort mesaRepository;

    @Override
    public List<Mesa> listarMesas() {
        return mesaRepository.listarTodas();
    }
}
