package com.lachozag4.pisip.infraestructura.controladores;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.ConsultarMesasPort;
import com.lachozag4.pisip.dominio.entidades.Mesa;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
@RequiredArgsConstructor
public class MesaController {

    private final ConsultarMesasPort consultarMesas;

    @GetMapping
    public List<Mesa> listarMesas() {
        return consultarMesas.listarMesas();
    }
}
