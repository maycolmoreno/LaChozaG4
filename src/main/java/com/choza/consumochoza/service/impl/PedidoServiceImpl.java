package com.choza.consumochoza.service.impl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.choza.consumochoza.modelo.dto.PedidoDTO;
import com.choza.consumochoza.modelo.dto.PedidosPaginadosDTO;
import com.choza.consumochoza.service.IPedidoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements IPedidoService {

    private final WebClient webClient;
    private static final String ENDPOINT = "/pedidos";

    @Override
    public List<PedidoDTO> listarTodos() {
        return webClient.get()
                .uri(ENDPOINT)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<PedidoDTO>>() {})
                .block();
    }

    /**
     * Paginación y filtrado en SERVIDOR — llama al endpoint /api/pedidos/paginado
     * del backend pisip en lugar de descargar todo y filtrar en memoria.
     */
    @Override
    public PedidosPaginadosDTO listarConFiltros(String estado, String busqueda,
                                                 LocalDate fechaDesde, LocalDate fechaHasta,
                                                 int page, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(ENDPOINT + "/paginado")
                        .queryParamIfPresent("estado",
                                Optional.ofNullable(estado).filter(s -> !s.isBlank() && !"TODOS".equalsIgnoreCase(s)))
                        .queryParamIfPresent("q",
                                Optional.ofNullable(busqueda).filter(s -> !s.isBlank()))
                        .queryParamIfPresent("fechaDesde",
                                Optional.ofNullable(fechaDesde))
                        .queryParamIfPresent("fechaHasta",
                                Optional.ofNullable(fechaHasta))
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .bodyToMono(PedidosPaginadosDTO.class)
                .block();
    }

    @Override
    public PedidoDTO obtenerPorId(int id) {
        return webClient.get()
                .uri(ENDPOINT + "/{id}", id)
                .retrieve()
                .bodyToMono(PedidoDTO.class)
                .block();
    }

    @Override
    public PedidoDTO crear(PedidoDTO pedido) {
        return webClient.post()
                .uri(ENDPOINT)
                .bodyValue(pedido)
                .retrieve()
                .bodyToMono(PedidoDTO.class)
                .block();
    }

    @Override
    public PedidoDTO actualizar(int id, PedidoDTO pedido) {
        return webClient.put()
                .uri(ENDPOINT + "/{id}", id)
                .bodyValue(pedido)
                .retrieve()
                .bodyToMono(PedidoDTO.class)
                .block();
    }

    @Override
    public PedidoDTO cambiarEstado(int id, String estado) {
        return webClient.put()
                .uri(ENDPOINT + "/{id}/estado", id)
                .bodyValue(java.util.Map.of("estado", estado))
                .retrieve()
                .bodyToMono(PedidoDTO.class)
                .block();
    }

    @Override
    public void eliminar(int id) {
        webClient.delete()
                .uri(ENDPOINT + "/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
