package com.choza.consumochoza.controlador;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.choza.consumochoza.modelo.dto.ComedorDTO;
import com.choza.consumochoza.modelo.dto.MesaDTO;
import com.choza.consumochoza.service.IComedorService;
import com.choza.consumochoza.service.IMesaService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mesa")
@RequiredArgsConstructor
public class MesaControlador {

    private final IMesaService mesaService;
    private final IComedorService comedorService;

    private void cargarDatosListado(Model model, List<MesaDTO> mesas) {
        List<MesaDTO> disponibles = mesaService.listarDisponibles();
        List<MesaDTO> ocupadas = mesaService.listarOcupadas();
        List<ComedorDTO> comedores = comedorService.listarActivos();

        // Pre-agrupar mesas por comedor para evitar SpEL en la plantilla
        Map<Integer, List<MesaDTO>> mesasPorComedor = new LinkedHashMap<>();
        List<MesaDTO> mesasSinComedor = new ArrayList<>();

        for (ComedorDTO c : comedores) {
            mesasPorComedor.put(c.getIdcomedor(), new ArrayList<>());
        }
        for (MesaDTO m : mesas) {
            if (m.getIdcomedor() != null && mesasPorComedor.containsKey(m.getIdcomedor())) {
                mesasPorComedor.get(m.getIdcomedor()).add(m);
            } else {
                mesasSinComedor.add(m);
            }
        }

        model.addAttribute("mesas", mesas);
        model.addAttribute("mesasDisponibles", disponibles);
        model.addAttribute("mesasOcupadas", ocupadas);
        model.addAttribute("comedores", comedores);
        model.addAttribute("mesasPorComedor", mesasPorComedor);
        model.addAttribute("mesasSinComedor", mesasSinComedor);
    }

    @GetMapping
    public String listar(Model model) {
        cargarDatosListado(model, mesaService.listarTodas());
        model.addAttribute("filtro", "todas");
        return "Mesa/MesaM";
    }

    @GetMapping("/disponibles")
    public String listarDisponibles(Model model) {
        cargarDatosListado(model, mesaService.listarDisponibles());
        model.addAttribute("filtro", "disponibles");
        return "Mesa/MesaM";
    }

    @GetMapping("/ocupadas")
    public String listarOcupadas(Model model) {
        cargarDatosListado(model, mesaService.listarOcupadas());
        model.addAttribute("filtro", "ocupadas");
        return "Mesa/MesaM";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("mesa", new MesaDTO());
        model.addAttribute("comedores", comedorService.listarActivos());
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
            model.addAttribute("comedores", comedorService.listarActivos());
            return "Mesa/MesaForm";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        model.addAttribute("mesa", mesaService.obtenerPorId(id));
        model.addAttribute("comedores", comedorService.listarActivos());
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
