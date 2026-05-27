package com.lachozag4.pisip.presentacion.controladores;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IReporteUseCase;
import com.lachozag4.pisip.aplicacion.casosuso.resultado.ReporteVentasDiaResultado;
import com.lachozag4.pisip.presentacion.dto.response.ReporteVentasDiaResponseDTO;
import com.lachozag4.pisip.presentacion.dto.response.ResumenProductoVentaDTO;
import com.lachozag4.pisip.presentacion.mapeadores.IPedidoDtoMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/reportes", produces = "application/json")
@RequiredArgsConstructor
public class ReporteControlador {

    private final IReporteUseCase reporteUseCase;
    private final IPedidoDtoMapper pedidoDtoMapper;

    @GetMapping("/ventas-dia")
    public ResponseEntity<ReporteVentasDiaResponseDTO> obtenerVentasDia(
            @RequestParam(name = "fecha", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now();

        ReporteVentasDiaResultado resultado = reporteUseCase.obtenerVentasDia(fechaConsulta);

        List<ResumenProductoVentaDTO> productosDto = resultado.productosMasVendidos().stream()
                .map(r -> {
                    ResumenProductoVentaDTO dto = new ResumenProductoVentaDTO();
                    dto.setIdProducto(r.idProducto());
                    dto.setNombreProducto(r.nombreProducto());
                    dto.setCantidadVendida(r.cantidadVendida());
                    dto.setTotalVendido(r.totalVendido());
                    return dto;
                })
                .toList();

        ReporteVentasDiaResponseDTO respuesta = new ReporteVentasDiaResponseDTO();
        respuesta.setFecha(resultado.fecha());
        respuesta.setTotalVentas(resultado.totalVentas());
        respuesta.setNumeroPedidos(resultado.numeroPedidos());
        respuesta.setTicketPromedio(resultado.ticketPromedio());
        respuesta.setTotalEfectivo(resultado.totalEfectivo());
        respuesta.setTotalTarjeta(resultado.totalTarjeta());
        respuesta.setTotalTransferencias(resultado.totalTransferencias());
        respuesta.setTotalOtros(resultado.totalOtros());
        respuesta.setTotalProductos(resultado.totalProductos());
        respuesta.setPedidos(resultado.pedidos().stream().map(pedidoDtoMapper::toResponseDTO).toList());
        respuesta.setProductos(productosDto);

        return ResponseEntity.ok(respuesta);
    }
}

