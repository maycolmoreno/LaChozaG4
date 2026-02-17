package com.lachozag4.pisip.presentacion.controladores;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lachozag4.pisip.aplicacion.casosuso.entradas.IPedidoUseCase;
import com.lachozag4.pisip.dominio.entidades.Pedido;
import com.lachozag4.pisip.dominio.entidades.PedidoDetalle;
import com.lachozag4.pisip.dominio.entidades.Producto;
import com.lachozag4.pisip.presentacion.dto.response.PedidoResponseDTO;
import com.lachozag4.pisip.presentacion.dto.response.ReporteVentasDiaResponseDTO;
import com.lachozag4.pisip.presentacion.dto.response.ResumenProductoVentaDTO;
import com.lachozag4.pisip.presentacion.mapeadores.IPedidoDtoMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/reportes", produces = "application/json")
@RequiredArgsConstructor
public class ReporteControlador {

    private final IPedidoUseCase pedidoUseCase;
    private final IPedidoDtoMapper pedidoDtoMapper;

    @GetMapping("/ventas-dia")
    public ResponseEntity<ReporteVentasDiaResponseDTO> obtenerVentasDia(
            @RequestParam(name = "fecha", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now();

        List<Pedido> pedidos = pedidoUseCase.listar();

        List<Pedido> pedidosDia = pedidos.stream()
                .filter(p -> p.getFecha() != null
                        && p.getFecha().toLocalDate().isEqual(fechaConsulta)
                        && Pedido.ESTADO_COMPLETADO.equals(p.getEstado()))
                .toList();

        // Totales simples
        int numeroPedidos = pedidosDia.size();
        int totalProductos = pedidosDia.stream()
                .flatMap(p -> p.getDetalles().stream())
                .mapToInt(PedidoDetalle::getCantidad)
                .sum();

        double totalVentas = pedidosDia.stream()
                .flatMap(p -> p.getDetalles().stream())
                .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                .sum();

        double ticketPromedio = numeroPedidos > 0 ? totalVentas / numeroPedidos : 0.0;

        // Lista de pedidos en formato de respuesta ya existente
        List<PedidoResponseDTO> pedidosDto = pedidosDia.stream()
                .map(pedidoDtoMapper::toResponseDTO)
                .toList();

        // Resumen por producto
        Map<Integer, ResumenProductoVentaDTO> resumenPorProducto = new HashMap<>();

        for (Pedido pedido : pedidosDia) {
            for (PedidoDetalle detalle : pedido.getDetalles()) {
                Producto prod = detalle.getFkProducto();
                if (prod == null) {
                    continue;
                }
                int idProd = prod.getIdproducto();
                ResumenProductoVentaDTO resumen = resumenPorProducto.computeIfAbsent(idProd, id -> {
                    ResumenProductoVentaDTO r = new ResumenProductoVentaDTO();
                    r.setIdProducto(id);
                    r.setNombreProducto(prod.getNombre());
                    r.setCantidadVendida(0);
                    r.setTotalVendido(0.0);
                    return r;
                });

                resumen.setCantidadVendida(resumen.getCantidadVendida() + detalle.getCantidad());
                resumen.setTotalVendido(resumen.getTotalVendido()
                        + (detalle.getCantidad() * detalle.getPrecioUnitario()));
            }
        }

        List<ResumenProductoVentaDTO> productos = new ArrayList<>(resumenPorProducto.values());
        productos.sort(Comparator.comparing(ResumenProductoVentaDTO::getTotalVendido).reversed());

        // Construir respuesta
        ReporteVentasDiaResponseDTO respuesta = new ReporteVentasDiaResponseDTO();
        respuesta.setFecha(fechaConsulta);
        respuesta.setTotalVentas(totalVentas);
        respuesta.setNumeroPedidos(numeroPedidos);
        respuesta.setTicketPromedio(ticketPromedio);
        respuesta.setTotalProductos(totalProductos);
        respuesta.setPedidos(pedidosDto);
        respuesta.setProductos(productos);

        return ResponseEntity.ok(respuesta);
    }
}
