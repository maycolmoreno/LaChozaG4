package com.choza.consumochoza.controlador;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.choza.consumochoza.service.IReporteService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReportesControlador {

    private final IReporteService reporteService;

    @GetMapping
    public String index() {
        // Cuando se accede a "/reportes" sin más, redirigimos al reporte de ventas del día
        return "redirect:/reportes/ventas-dia";
    }

    @GetMapping("/ventas-dia")
    public String verReporteVentasDia(
            @RequestParam(name = "fecha", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Model model) {

        LocalDate fechaConsulta = (fecha != null) ? fecha : LocalDate.now();

        var reporte = reporteService.obtenerVentasDia(fechaConsulta);

        model.addAttribute("reporte", reporte);
        model.addAttribute("fechaSeleccionada", fechaConsulta);

        return "Reporte/VentasDia";
    }
}
