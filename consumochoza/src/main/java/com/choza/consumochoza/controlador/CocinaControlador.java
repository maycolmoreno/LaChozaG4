package com.choza.consumochoza.controlador;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.choza.consumochoza.modelo.dto.PedidoDetalleDTO;
import com.choza.consumochoza.service.ICategoriaService;
import com.choza.consumochoza.service.IPedidoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/cocina")
@RequiredArgsConstructor
public class CocinaControlador {

    private final IPedidoService pedidoService;
    private final ICategoriaService categoriaService;

    @GetMapping
    public String verOrdenes(Model model) {
        try {
            // Categorias BAR: productos que no requieren preparacion en cocina.
            Set<Integer> categoriasBarIds = categoriaService.listarTodas().stream()
                    .filter(c -> c.getNombre() != null && c.getNombre().trim().equalsIgnoreCase("BAR"))
                    .map(c -> c.getIdcategoria())
                    .collect(Collectors.toSet());

            // Pedidos en cocina, excluyendo items BAR.
            var pedidos = pedidoService.listarTodos().stream()
                    .filter(p -> "EN_COCINA".equals(p.getEstado()))
                    .peek(p -> {
                        if (p.getDetalle() == null) {
                            return;
                        }
                        var detalleCocina = p.getDetalle().stream()
                                .filter(this::esItemValido)
                                .filter(det -> det.getProducto() == null
                                        || !categoriasBarIds.contains(det.getProducto().getCategoriaId()))
                                .toList();
                        p.setDetalle(detalleCocina);
                    })
                    .filter(p -> p.getDetalle() != null && !p.getDetalle().isEmpty())
                    .toList();
            model.addAttribute("pedidos", pedidos);
        } catch (Exception ex) {
            model.addAttribute("pedidos", java.util.List.of());
            model.addAttribute("mensajeError", ex.getMessage());
        }
        return "Cocina/Ordenes";
    }

    private boolean esItemValido(PedidoDetalleDTO detalle) {
        return detalle != null && detalle.getCantidad() > 0;
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
