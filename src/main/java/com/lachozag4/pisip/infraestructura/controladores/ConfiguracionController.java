package com.lachozag4.pisip.infraestructura.controladores;

import com.lachozag4.pisip.infraestructura.persistencia.jpa.ConfiguracionRepository;
import com.lachozag4.pisip.dominio.entidades.Configuracion;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/configuracion")
public class ConfiguracionController {

    private final ConfiguracionRepository configRepo;

    public ConfiguracionController(ConfiguracionRepository configRepo) {
        this.configRepo = configRepo;
    }

    @PutMapping
    public Configuracion actualizarIva(@RequestBody Configuracion nuevaConfig) {
        // Lógica para actualizar el valor del IVA en la base de datos
        return configRepo.save(nuevaConfig);
    }
}