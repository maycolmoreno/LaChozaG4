package com.lachozag4.pisip.aplicacion.puertos.salida;

import com.lachozag4.pisip.dominio.entidades.Configuracion;
import java.util.Optional;

public interface ConfiguracionRepositoryPort {
    Optional<Configuracion> obtenerConfiguracionActual();
}