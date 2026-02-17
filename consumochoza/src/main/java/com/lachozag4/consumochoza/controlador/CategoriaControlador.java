package com.lachozag4.consumochoza.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lachozag4.consumochoza.modelo.dto.CategoriaDTO;
import com.lachozag4.consumochoza.service.ICategoriaService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/categoria")
@RequiredArgsConstructor
public class CategoriaControlador {

    private final ICategoriaService categoriaService;

    // Listar todas las categorÃ­as
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "Categorias/CategoriaM";
    }

    // Formulario para crear nueva categorÃ­a
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("categoria", new CategoriaDTO());
        return "Categorias/CategoriaForm";
    }

    // Guardar nueva categorÃ­a
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute CategoriaDTO categoria, Model model) {
        try {
            if (categoria.getIdcategoria() == 0) {
                categoriaService.crear(categoria);
            } else {
                categoriaService.actualizar(categoria.getIdcategoria(), categoria);
            }
            return "redirect:/categoria";
        } catch (Exception e) {
            String mensaje = e.getMessage();
            if (mensaje != null && mensaje.toLowerCase().contains("nombre")) {
                model.addAttribute("mensajeError", "âš ï¸ Ya existe una categorÃ­a con ese nombre. Por favor ingrese uno diferente.");
            } else {
                model.addAttribute("mensajeError", "âš ï¸ " + (mensaje != null ? mensaje : "Error al guardar la categorÃ­a"));
            }
            model.addAttribute("categoria", categoria);
            return "Categorias/CategoriaForm";
        }
    }

    // Formulario para editar categorÃ­a
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        model.addAttribute("categoria", categoriaService.obtenerPorId(id));
        return "Categorias/CategoriaForm";
    }

    // Dar de baja categorÃ­a
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            categoriaService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "CategorÃ­a dada de baja correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se pudo dar de baja la categorÃ­a. " + e.getMessage());
        }
        return "redirect:/categoria";
    }
}
