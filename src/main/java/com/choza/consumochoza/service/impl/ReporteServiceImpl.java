package com.choza.consumochoza.service.impl;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.choza.consumochoza.modelo.dto.ReporteVentasDiaDTO;
import com.choza.consumochoza.service.IReporteService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements IReporteService {

    private final WebClient webClient;

    @Override
    public ReporteVentasDiaDTO obtenerVentasDia(LocalDate fecha) {
        String endpoint = "/reportes/ventas-dia";
        if (fecha != null) {
            endpoint += "?fecha=" + fecha.toString();
        }
        return webClient.get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(ReporteVentasDiaDTO.class)
                .block();
    }
}
