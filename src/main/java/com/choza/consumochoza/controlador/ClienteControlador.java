package com.choza.consumochoza.controlador;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.choza.consumochoza.modelo.dto.ClienteDTO;
import com.choza.consumochoza.service.IClienteService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/cliente")
@RequiredArgsConstructor
public class ClienteControlador {

    private final IClienteService clienteService;

    @GetMapping
    public String listar(
            @RequestParam(name = "estado", required = false) String estado,
            @RequestParam(name = "q", required = false) String q,
            Model model) {

        List<ClienteDTO> clientes = clienteService.listarTodos();

        // Filtro por estado (ACTIVO / INACTIVO / TODOS)
        if (estado != null && !estado.isBlank()) {
            String estadoUpper = estado.toUpperCase();
            if ("ACTIVO".equals(estadoUpper)) {
                clientes = clientes.stream()
                        .filter(ClienteDTO::isEstado)
                        .collect(Collectors.toList());
            } else if ("INACTIVO".equals(estadoUpper)) {
                clientes = clientes.stream()
                        .filter(c -> !c.isEstado())
                        .collect(Collectors.toList());
            }
        }

        // Búsqueda por nombre, cédula, teléfono o email
        if (q != null && !q.trim().isEmpty()) {
            String termino = q.trim().toLowerCase();
            clientes = clientes.stream()
                    .filter(c ->
                            (c.getNombre() != null && c.getNombre().toLowerCase().contains(termino)) ||
                            (c.getCedula() != null && c.getCedula().toLowerCase().contains(termino)) ||
                            (c.getTelefono() != null && c.getTelefono().toLowerCase().contains(termino)) ||
                            (c.getEmail() != null && c.getEmail().toLowerCase().contains(termino)))
                    .collect(Collectors.toList());
        }

        model.addAttribute("clientes", clientes);
        model.addAttribute("filtroEstado", (estado == null || estado.isBlank()) ? "TODOS" : estado.toUpperCase());
        model.addAttribute("q", q);

        return "Cliente/ClienteM";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("cliente", new ClienteDTO());
        return "Cliente/ClienteForm";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute ClienteDTO cliente, Model model) {
        try {
            if (cliente.getIdcliente() == 0) {
                clienteService.crear(cliente);
            } else {
                clienteService.actualizar(cliente.getIdcliente(), cliente);
            }
            return "redirect:/cliente";
        } catch (Exception e) {
            // Validación específica para datos duplicados
            String mensaje = e.getMessage();
            if (mensaje != null && (mensaje.toLowerCase().contains("email") || mensaje.toLowerCase().contains("correo"))) {
                model.addAttribute("mensajeError", "⚠️ El correo ingresado ya está registrado. Por favor ingrese uno diferente.");
            } else if (mensaje != null && mensaje.toLowerCase().contains("cedula")) {
                model.addAttribute("mensajeError", "⚠️ La cédula ingresada ya está registrada. Por favor ingrese una diferente.");
            } else {
                model.addAttribute("mensajeError", "⚠️ " + (mensaje != null ? mensaje : "Error al guardar el cliente"));
            }
            model.addAttribute("cliente", cliente);
            return "Cliente/ClienteForm";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        model.addAttribute("cliente", clienteService.obtenerPorId(id));
        return "Cliente/ClienteForm";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            clienteService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Cliente dado de baja correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se pudo dar de baja el cliente. " + e.getMessage());
        }
        return "redirect:/cliente";
    }
}