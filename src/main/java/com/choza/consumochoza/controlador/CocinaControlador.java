package com.choza.consumochoza.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.choza.consumochoza.service.IPedidoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/cocina")
@RequiredArgsConstructor
public class CocinaControlador {

    private final IPedidoService pedidoService;

    @GetMapping
    public String verOrdenes(Model model) {
        // Obtener pedidos que están en cocina
        var pedidos = pedidoService.listarTodos().stream()
                .filter(p -> "EN_COCINA".equals(p.getEstado()))
                .toList();
        
        model.addAttribute("pedidos", pedidos);
        return "Cocina/Ordenes";
    }

    @PostMapping("/completar/{id}")
    public String completarOrden(@PathVariable int id) {
        try {
            // Cambiar estado a LISTO_PARA_ENTREGA (listo para llevar a la mesa)
            pedidoService.cambiarEstado(id, "LISTO_PARA_ENTREGA");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/cocina";
    }
}
