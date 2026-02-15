package com.choza.consumochoza.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.choza.consumochoza.modelo.dto.MesaDTO;
import com.choza.consumochoza.service.IMesaService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mesa")
@RequiredArgsConstructor
public class MesaControlador {

    private final IMesaService mesaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("mesas", mesaService.listarTodas());
        model.addAttribute("mesasDisponibles", mesaService.listarDisponibles());
        model.addAttribute("mesasOcupadas", mesaService.listarOcupadas());
        model.addAttribute("filtro", "todas");
        return "Mesa/MesaM";
    }

    @GetMapping("/disponibles")
    public String listarDisponibles(Model model) {
        model.addAttribute("mesas", mesaService.listarDisponibles());
        model.addAttribute("mesasDisponibles", mesaService.listarDisponibles());
        model.addAttribute("mesasOcupadas", mesaService.listarOcupadas());
        model.addAttribute("filtro", "disponibles");
        return "Mesa/MesaM";
    }

    @GetMapping("/ocupadas")
    public String listarOcupadas(Model model) {
        model.addAttribute("mesas", mesaService.listarOcupadas());
        model.addAttribute("mesasDisponibles", mesaService.listarDisponibles());
        model.addAttribute("mesasOcupadas", mesaService.listarOcupadas());
        model.addAttribute("filtro", "ocupadas");
        return "Mesa/MesaM";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("mesa", new MesaDTO());
        return "Mesa/MesaForm";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute MesaDTO mesa, Model model) {
        try {
            if (mesa.getIdmesa() == 0) {
                mesaService.crear(mesa);
            } else {
                mesaService.actualizar(mesa.getIdmesa(), mesa);
            }
            return "redirect:/mesa";
        } catch (Exception e) {
            String mensaje = e.getMessage();
            if (mensaje != null && mensaje.toLowerCase().contains("numero") || mensaje != null && mensaje.toLowerCase().contains("número")) {
                model.addAttribute("mensajeError", "⚠️ Ya existe una mesa con ese número. Por favor ingrese uno diferente.");
            } else {
                model.addAttribute("mensajeError", "⚠️ " + (mensaje != null ? mensaje : "Error al guardar la mesa"));
            }
            model.addAttribute("mesa", mesa);
            return "Mesa/MesaForm";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        model.addAttribute("mesa", mesaService.obtenerPorId(id));
        return "Mesa/MesaForm";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            mesaService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Mesa dada de baja correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se pudo dar de baja la mesa. " + e.getMessage());
        }
        return "redirect:/mesa";
    }
}
