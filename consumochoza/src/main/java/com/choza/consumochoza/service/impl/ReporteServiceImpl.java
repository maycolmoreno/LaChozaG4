package com.choza.consumochoza.service.impl;

import java.time.Duration;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.choza.consumochoza.modelo.dto.ReporteVentasDiaDTO;
import com.choza.consumochoza.service.IReporteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReporteServiceImpl implements IReporteService {

    private final WebClient webClient;
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

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
                .block(TIMEOUT);
    }
}
