package com.choza.consumochoza.controlador;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.choza.consumochoza.modelo.dto.UsuarioDTO;
import com.choza.consumochoza.service.IUsuarioService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthControlador {

    private final IUsuarioService usuarioService;

    @GetMapping("/login")
    public String login() {
        // Si no existe ningún usuario en el sistema, redirigimos al
        // asistente de creación de administrador inicial.
        if (!usuarioService.existeAlgunUsuario()) {
            return "redirect:/setup-admin";
        }
        return "login";
    }
    
    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "error/403";
    }
    
    @GetMapping("/")
    public String home(Authentication auth) {
        // Redirigir según el rol del usuario
        if (auth != null) {
            if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_COCINA"))) {
                return "redirect:/cocina";
            } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CAMARERO"))) {
                return "redirect:/pedidos";
            } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                return "redirect:/dashboard";
            }
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/setup-admin")
    public String mostrarSetupAdmin(Model model) {
        // Si ya existe al menos un usuario, volvemos al login normal
        if (usuarioService.existeAlgunUsuario()) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", new UsuarioDTO());
        return "Usuario/SetupAdmin";
    }

    @PostMapping("/setup-admin")
    public String registrarAdminInicial(@ModelAttribute("usuario") UsuarioDTO usuario, Model model) {
        // Si ya se creó alguien entre medias, mandamos a login
        if (usuarioService.existeAlgunUsuario()) {
            return "redirect:/login";
        }
        try {
            usuario.setRol("ADMIN");
            usuario.setEstado(true);
            usuarioService.crearAdminInicial(usuario);
            return "redirect:/login?adminCreado=true";
        } catch (Exception e) {
            model.addAttribute("mensajeError",
                    e.getMessage() != null ? e.getMessage() : "No se pudo crear el administrador inicial.");
            return "Usuario/SetupAdmin";
        }
    }
}
