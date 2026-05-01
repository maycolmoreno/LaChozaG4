package com.choza.consumochoza.controlador;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.choza.consumochoza.modelo.dto.CategoriaDTO;
import com.choza.consumochoza.modelo.dto.ProductoDTO;
import com.choza.consumochoza.service.ICategoriaService;
import com.choza.consumochoza.service.IProductoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuDigitalControlador {

    private final IProductoService productoService;
    private final ICategoriaService categoriaService;

    @GetMapping
    public String verMenu(Model model) {
        List<CategoriaDTO> categorias = categoriaService.listarActivas();
        List<ProductoDTO> productos = productoService.listarActivos();

        // Agrupar productos por categoria
        Map<CategoriaDTO, List<ProductoDTO>> menu = new LinkedHashMap<>();
        for (CategoriaDTO cat : categorias) {
            List<ProductoDTO> productosCat = productos.stream()
                    .filter(p -> p.getCategoriaId() == cat.getIdcategoria())
                    .toList();
            if (!productosCat.isEmpty()) {
                menu.put(cat, productosCat);
            }
        }

        model.addAttribute("menu", menu);
        return "Menu/MenuDigital";
    }
}
