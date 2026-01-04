package com.lachozag4.pisip.infraestructura.controladores;

import com.lachozag4.pisip.aplicacion.puertos.salida.ProductoRepositoryPort;
import com.lachozag4.pisip.dominio.entidades.Producto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class VistaController {
    
    private final ProductoRepositoryPort productoRepo;
    
    public VistaController(ProductoRepositoryPort productoRepo) {
        this.productoRepo = productoRepo;
    }
    
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("contenido", "index :: contenido");
        return "base";
    }
    
    @GetMapping("/producto")
    public String producto(Model model) {
        model.addAttribute("contenido", "producto :: contenido");
        return "base";
    }
    
    @GetMapping("/agregar-producto")
    public String agregarProducto(Model model) {
        model.addAttribute("contenido", "form-producto :: contenido");
        model.addAttribute("producto", null);
        return "base";
    }
    
    @GetMapping("/editar-producto/{id}")
    public String editarProducto(@PathVariable Long id, Model model) {
        Producto producto = productoRepo.buscarPorId(id).orElse(null);
        
        if (producto == null) {
            return "redirect:/producto";
        }
        
        model.addAttribute("contenido", "form-producto :: contenido");
        model.addAttribute("producto", producto);
        return "base";
    }
    
    @GetMapping("/categoria")
    public String paginaCategorias(Model model) {
        model.addAttribute("contenido", "categoria :: contenido");
        return "base";
    }
    
    @GetMapping("/mesas")
    public String mesas(Model model) {
        model.addAttribute("contenido", "mesas :: contenido");
        return "base";
    }
    
    @GetMapping("/clientes")
    public String paginaClientes(Model model) {
        model.addAttribute("contenido", "clientes :: contenido");
        return "base";
    }
    
    @GetMapping("/pedido")
    public String pedido(org.springframework.ui.Model model) {
        model.addAttribute("contenido", "pedido :: contenido");
        return "base";
    }

}