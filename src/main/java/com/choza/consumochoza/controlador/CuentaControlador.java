package com.choza.consumochoza.controlador;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.choza.consumochoza.modelo.dto.CambiarPasswordDTO;
import com.choza.consumochoza.service.IUsuarioService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mi-cuenta")
@RequiredArgsConstructor
public class CuentaControlador {

    private final IUsuarioService usuarioService;

    @GetMapping("/password")
    public String mostrarFormularioCambioPassword(Model model) {
        model.addAttribute("form", new CambiarPasswordDTO());
        return "Usuario/CambiarPassword";
    }

    @PostMapping("/password")
    public String cambiarPassword(@ModelAttribute("form") CambiarPasswordDTO form,
                                  Authentication authentication,
                                  Model model) {
        String username = authentication.getName();

        if (form.getPasswordNuevo() == null || !form.getPasswordNuevo().equals(form.getPasswordConfirmacion())) {
            model.addAttribute("mensajeError", "La nueva contraseña y la confirmación no coinciden.");
            return "Usuario/CambiarPassword";
        }

        try {
            usuarioService.cambiarPassword(username, form.getPasswordActual(), form.getPasswordNuevo());
            model.addAttribute("mensajeExito", "Contraseña actualizada correctamente.");
            // Limpiar campos del formulario
            model.addAttribute("form", new CambiarPasswordDTO());
        } catch (Exception e) {
            model.addAttribute("mensajeError", e.getMessage() != null ? e.getMessage() : "Error al cambiar la contraseña.");
        }

        return "Usuario/CambiarPassword";
    }
}
