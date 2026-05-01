package com.choza.consumochoza.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.choza.consumochoza.modelo.dto.CategoriaDTO;
import com.choza.consumochoza.service.ICategoriaService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/categoria")
@RequiredArgsConstructor
public class CategoriaControlador {

    private final ICategoriaService categoriaService;

    // Listar todas las categorías
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "Categorias/CategoriaM";
    }

    // Formulario para crear nueva categoría
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("categoria", new CategoriaDTO());
        return "Categorias/CategoriaForm";
    }

    // Guardar nueva categoría
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
                model.addAttribute("mensajeError", "⚠️ Ya existe una categoría con ese nombre. Por favor ingrese uno diferente.");
            } else {
                model.addAttribute("mensajeError", "⚠️ " + (mensaje != null ? mensaje : "Error al guardar la categoría"));
            }
            model.addAttribute("categoria", categoria);
            return "Categorias/CategoriaForm";
        }
    }

    // Formulario para editar categoría
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        model.addAttribute("categoria", categoriaService.obtenerPorId(id));
        return "Categorias/CategoriaForm";
    }

    // Dar de baja categoría
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            categoriaService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Categoría dada de baja correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se pudo dar de baja la categoría. " + e.getMessage());
        }
        return "redirect:/categoria";
    }
}