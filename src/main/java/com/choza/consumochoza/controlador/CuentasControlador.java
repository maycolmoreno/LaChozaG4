package com.choza.consumochoza.controlador;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.choza.consumochoza.modelo.dto.CuentaDTO;
import com.choza.consumochoza.service.ICuentaService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentasControlador {

    private final ICuentaService cuentaService;

    @GetMapping
    public String listarCuentas(
            @org.springframework.web.bind.annotation.RequestParam(name = "estado", required = false) String estado,
            @org.springframework.web.bind.annotation.RequestParam(name = "fechaDesde", required = false) String fechaDesde,
            @org.springframework.web.bind.annotation.RequestParam(name = "fechaHasta", required = false) String fechaHasta,
            Model model) {

        List<CuentaDTO> cuentas = cuentaService.listarTodas();

        // Filtro por estado (ABIERTA, PAGADA, ANULADA)
        if (estado != null && !estado.isBlank() && !"TODAS".equalsIgnoreCase(estado)) {
            String estadoUpper = estado.toUpperCase();
            cuentas = cuentas.stream()
                    .filter(c -> c.getEstado() != null && c.getEstado().equalsIgnoreCase(estadoUpper))
                    .toList();
        }

        // Filtro por rango de fechas de apertura
        LocalDate desde = null;
        LocalDate hasta = null;
        if (fechaDesde != null && !fechaDesde.isBlank()) {
            desde = LocalDate.parse(fechaDesde);
        }
        if (fechaHasta != null && !fechaHasta.isBlank()) {
            hasta = LocalDate.parse(fechaHasta);
        }
        if (desde != null || hasta != null) {
            LocalDate finalDesde = desde;
            LocalDate finalHasta = hasta;
            cuentas = cuentas.stream()
                    .filter(c -> {
                        if (c.getFechaApertura() == null) return false;
                        LocalDate f = c.getFechaApertura().toLocalDate();
                        boolean okDesde = finalDesde == null || !f.isBefore(finalDesde);
                        boolean okHasta = finalHasta == null || !f.isAfter(finalHasta);
                        return okDesde && okHasta;
                    })
                    .toList();
        }

        model.addAttribute("cuentas", cuentas);
        model.addAttribute("filtroEstado", (estado == null || estado.isBlank()) ? "TODAS" : estado.toUpperCase());
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);

        return "Cuenta/Cuentas";
    }

    @PostMapping("/cobrar/{id}")
    public String cobrarCuenta(
            @PathVariable("id") int idcuenta,
            @org.springframework.web.bind.annotation.RequestParam(name = "origen", required = false) String origen,
            RedirectAttributes redirectAttributes) {
        try {
            cuentaService.cambiarEstado(idcuenta, "PAGADA");
            redirectAttributes.addFlashAttribute("mensajeExito", "Cuenta cobrada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo cobrar la cuenta: " + e.getMessage());
        }
        // Si viene desde la lista de pedidos, regresar allí; de lo contrario, a cuentas
        if (origen != null && origen.equalsIgnoreCase("pedidos")) {
            return "redirect:/pedidos";
        }
        return "redirect:/cuentas";
    }

    @PostMapping("/anular/{id}")
    public String anularCuenta(@PathVariable("id") int idcuenta, RedirectAttributes redirectAttributes) {
        try {
            cuentaService.cambiarEstado(idcuenta, "ANULADA");
            redirectAttributes.addFlashAttribute("mensajeExito", "Cuenta anulada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo anular la cuenta: " + e.getMessage());
        }
        return "redirect:/cuentas";
    }
}
