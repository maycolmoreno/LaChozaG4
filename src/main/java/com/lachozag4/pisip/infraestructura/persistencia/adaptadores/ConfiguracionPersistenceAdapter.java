package com.lachozag4.pisip.infraestructura.persistencia.adaptadores;

import com.lachozag4.pisip.aplicacion.puertos.salida.ConfiguracionRepositoryPort;
import com.lachozag4.pisip.dominio.entidades.Configuracion;
import com.lachozag4.pisip.infraestructura.persistencia.jpa.ConfiguracionRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class ConfiguracionPersistenceAdapter implements ConfiguracionRepositoryPort {

    private final ConfiguracionRepository configuracionRepository;

    public ConfiguracionPersistenceAdapter(ConfiguracionRepository configuracionRepository) {
        this.configuracionRepository = configuracionRepository;
    }

    @Override
    public Optional<Configuracion> obtenerConfiguracionActual() {
        // Retorna la primera configuración encontrada (usualmente solo hay una fila)
        return configuracionRepository.findAll().stream().findFirst();
    }
}