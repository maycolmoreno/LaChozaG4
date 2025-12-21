package com.lachozag4.pisip.aplicacion.casosuso.impl;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.GestionarConfiguracionPort;
import com.lachozag4.pisip.dominio.entidades.Configuracion;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.ConfiguracionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GestionarConfiguracionUseCase implements GestionarConfiguracionPort {

    private final ConfiguracionRepository configRepo;

    @Override
    @Transactional(readOnly = true)
    public Double obtenerIvaActual() {
        return configRepo.findById(1L)
                .map(Configuracion::getPorcentajeIva)
                .orElse(15.0); // Valor por defecto si no hay config
    }

    @Override
    @Transactional
    public void actualizarPorcentajeIva(Double nuevoIva) {
        Configuracion config = configRepo.findById(1L)
                .orElseThrow(() -> new RuntimeException("No se encontró la configuración inicial."));
        
        config.setPorcentajeIva(nuevoIva);
        configRepo.save(config);
    }
}