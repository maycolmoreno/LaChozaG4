package com.choza.consumochoza.controlador;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.choza.consumochoza.modelo.dto.PedidoDTO;
import com.choza.consumochoza.modelo.dto.ProductoDTO;
import com.choza.consumochoza.modelo.dto.ReporteVentasDiaDTO;
import com.choza.consumochoza.service.IPedidoService;
import com.choza.consumochoza.service.IProductoService;
import com.choza.consumochoza.service.IReporteService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardControlador {

    private final IReporteService reporteService;
    private final IPedidoService pedidoService;
    private final IProductoService productoService;

    @GetMapping
    public String verDashboard(Model model) {
        LocalDate hoy = LocalDate.now();
        ReporteVentasDiaDTO reporteDia = reporteService.obtenerVentasDia(hoy);

        // Pedidos en curso: todos los que no están COMPLETADO ni CANCELADO
        List<PedidoDTO> pedidos = pedidoService.listarTodos();
        List<PedidoDTO> pedidosEnCurso = pedidos.stream()
                .filter(p -> p.getEstado() != null)
                .filter(p -> !"COMPLETADO".equalsIgnoreCase(p.getEstado()))
                .filter(p -> !"CANCELADO".equalsIgnoreCase(p.getEstado()))
                .collect(Collectors.toList());

        // Stock bajo: productos activos con stock menor a un umbral (ej. 10)
        List<ProductoDTO> productosActivos = productoService.listarActivos();
        int umbralStockBajo = 10;
        List<ProductoDTO> stockBajo = productosActivos.stream()
            .filter(p -> p.getStockActual() <= umbralStockBajo)
            .sorted(Comparator.comparingInt(ProductoDTO::getStockActual))
            .collect(Collectors.toList());

        model.addAttribute("reporteDia", reporteDia);
        model.addAttribute("pedidosEnCurso", pedidosEnCurso);
        model.addAttribute("stockBajo", stockBajo);

        return "Dashboard/AdminDashboard";
    }
}
