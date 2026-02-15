package com.choza.consumochoza.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.choza.consumochoza.modelo.dto.ProductoDTO;
import com.choza.consumochoza.service.ICategoriaService;
import com.choza.consumochoza.service.IProductoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/producto")
@RequiredArgsConstructor
public class ProductoControlador {

    private final IProductoService productoService;
    private final ICategoriaService categoriaService;

    @GetMapping
    public String listar(@RequestParam(name = "estado", required = false) String estado,
                         @RequestParam(name = "categoriaId", required = false) Integer categoriaId,
                         @RequestParam(name = "q", required = false) String q,
                         Model model) {

        java.util.List<ProductoDTO> productos;

        // 1) Filtro por categoría desde el backend
        if (categoriaId != null) {
            productos = productoService.listarPorCategoria(categoriaId);
        } else if ("activos".equalsIgnoreCase(estado)) {
            // 2) Solo activos
            productos = productoService.listarActivos();
        } else {
            // 3) Todos (activos + inactivos)
            productos = productoService.listarTodos();

            if ("inactivos".equalsIgnoreCase(estado)) {
                productos = productos.stream()
                        .filter(p -> !p.isEstado())
                        .toList();
            }
        }

        // 4) Búsqueda por nombre / descripción en memoria
        if (q != null && !q.isBlank()) {
            String termino = q.toLowerCase();
            productos = productos.stream()
                    .filter(p -> (p.getNombre() != null && p.getNombre().toLowerCase().contains(termino))
                              || (p.getDescripcion() != null && p.getDescripcion().toLowerCase().contains(termino)))
                    .toList();
        }

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaService.listarActivas());
        model.addAttribute("filtroEstado", estado == null ? "todos" : estado);
        model.addAttribute("filtroCategoriaId", categoriaId);
        model.addAttribute("q", q);
        return "Producto/ProductoM";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("producto", new ProductoDTO());
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "Producto/ProductoForm";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute ProductoDTO producto, Model model) {
        try {
            if (producto.getIdproducto() == 0) {
                productoService.crear(producto);
            } else {
                productoService.actualizar(producto.getIdproducto(), producto);
            }
            return "redirect:/producto";
        } catch (Exception e) {
            String mensaje = e.getMessage();
            if (mensaje != null && mensaje.toLowerCase().contains("nombre")) {
                model.addAttribute("mensajeError", "⚠️ Ya existe un producto con ese nombre en la categoría seleccionada. Por favor ingrese uno diferente.");
            } else {
                model.addAttribute("mensajeError", "⚠️ " + (mensaje != null ? mensaje : "Error al guardar el producto"));
            }
            model.addAttribute("producto", producto);
            model.addAttribute("categorias", categoriaService.listarTodas());
            return "Producto/ProductoForm";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        model.addAttribute("producto", productoService.obtenerPorId(id));
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "Producto/ProductoForm";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            productoService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Producto dado de baja correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se pudo dar de baja el producto. " + e.getMessage());
        }
        return "redirect:/producto";
    }
}
