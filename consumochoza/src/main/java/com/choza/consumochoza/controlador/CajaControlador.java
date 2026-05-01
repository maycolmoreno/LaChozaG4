package com.choza.consumochoza.controlador;

import java.util.List;
import java.util.Comparator;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.choza.consumochoza.modelo.dto.CajaTurnoDTO;
import com.choza.consumochoza.service.ICajaService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/caja")
@RequiredArgsConstructor
public class CajaControlador {

    private final ICajaService cajaService;

    @GetMapping
    public String verCaja(Model model) {
        CajaTurnoDTO cajaAbierta = null;
        try {
            cajaAbierta = cajaService.obtenerCajaAbierta();
        } catch (Exception ignored) {
            cajaAbierta = null;
        }

        List<CajaTurnoDTO> historial = cajaService.listarCajas().stream()
                .sorted(Comparator
                        .comparing(CajaTurnoDTO::getFechaApertura, Comparator.nullsLast(Comparator.naturalOrder()))
                        .reversed()
                        .thenComparing(CajaTurnoDTO::getIdcaja, Comparator.reverseOrder()))
                .toList();
        model.addAttribute("cajaAbierta", cajaAbierta);
        model.addAttribute("historialCajas", historial);
        return "Caja/Caja";
    }

    @PostMapping("/abrir")
    public String abrirCaja(@RequestParam("montoInicial") double montoInicial,
            @RequestParam(name = "observaciones", required = false) String observaciones,
            RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "sistema";

        try {
            cajaService.abrirCaja(montoInicial, username, observaciones);
            redirectAttributes.addFlashAttribute("mensajeExito", "Caja abierta correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo abrir la caja: " + e.getMessage());
        }
        return "redirect:/caja";
    }

    @PostMapping("/cerrar")
    public String cerrarCaja(@RequestParam("montoDeclaradoCierre") double montoDeclaradoCierre,
            @RequestParam(name = "observaciones", required = false) String observaciones,
            RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "sistema";

        try {
            cajaService.cerrarCaja(montoDeclaradoCierre, username, observaciones);
            redirectAttributes.addFlashAttribute("mensajeExito", "Caja cerrada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "No se pudo cerrar la caja: " + e.getMessage());
        }
        return "redirect:/caja";
    }
}
