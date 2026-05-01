package com.choza.consumochoza.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.choza.consumochoza.modelo.dto.ComedorDTO;
import com.choza.consumochoza.service.IComedorService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/comedor")
@RequiredArgsConstructor
public class ComedorControlador {

    private final IComedorService comedorService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("comedores", comedorService.listarTodos());
        return "Comedor/ComedorM";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("comedor", new ComedorDTO());
        return "Comedor/ComedorForm";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute ComedorDTO comedor, Model model) {
        try {
            if (comedor.getIdcomedor() == 0) {
                comedorService.crear(comedor);
            } else {
                comedorService.actualizar(comedor.getIdcomedor(), comedor);
            }
            return "redirect:/comedor";
        } catch (Exception e) {
            String mensaje = e.getMessage();
            if (mensaje != null && mensaje.toLowerCase().contains("nombre")) {
                model.addAttribute("mensajeError", "⚠️ Ya existe un comedor con ese nombre. Por favor ingrese uno diferente.");
            } else {
                model.addAttribute("mensajeError", "⚠️ " + (mensaje != null ? mensaje : "Error al guardar el comedor"));
            }
            model.addAttribute("comedor", comedor);
            return "Comedor/ComedorForm";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        model.addAttribute("comedor", comedorService.obtenerPorId(id));
        return "Comedor/ComedorForm";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            comedorService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Comedor dado de baja correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se pudo dar de baja el comedor. " + e.getMessage());
        }
        return "redirect:/comedor";
    }
}
