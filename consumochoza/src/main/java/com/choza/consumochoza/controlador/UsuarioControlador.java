package com.choza.consumochoza.controlador;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.choza.consumochoza.modelo.dto.UsuarioDTO;
import com.choza.consumochoza.service.IUsuarioService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioControlador {

    private final IUsuarioService usuarioService;
    private final SessionRegistry sessionRegistry;

    @GetMapping
    public String listar(@RequestParam(name = "estado", required = false) String estado, Model model) {
        java.util.List<UsuarioDTO> usuarios = usuarioService.listarTodos();

        if ("ACTIVO".equalsIgnoreCase(estado)) {
            usuarios = usuarios.stream().filter(UsuarioDTO::isEstado).toList();
        } else if ("INACTIVO".equalsIgnoreCase(estado)) {
            usuarios = usuarios.stream().filter(u -> !u.isEstado()).toList();
        }

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("filtroEstado", estado == null ? "TODOS" : estado.toUpperCase());
        return "Usuario/Usuariop";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("usuario", new UsuarioDTO());
        return "Usuario/UsuarioForm";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute UsuarioDTO usuario, Model model) {
        try {
            if (usuario.getIdusuario() == 0) {
                usuarioService.crear(usuario);
            } else {
                usuarioService.actualizar(usuario.getIdusuario(), usuario);
            }
            return "redirect:/usuario";
        } catch (Exception e) {
            String mensaje = e.getMessage();
            if (mensaje != null && mensaje.toLowerCase().contains("username")) {
                model.addAttribute("mensajeError", "⚠️ Ya existe un usuario con ese username. Por favor ingrese uno diferente.");
            } else {
                model.addAttribute("mensajeError", "⚠️ " + (mensaje != null ? mensaje : "Error al guardar el usuario"));
            }
            model.addAttribute("usuario", usuario);
            return "Usuario/UsuarioForm";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        model.addAttribute("usuario", usuarioService.obtenerPorId(id));
        return "Usuario/UsuarioForm";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            // Obtener datos del usuario antes de darlo de baja para localizar sus sesiones activas
            UsuarioDTO usuario = usuarioService.obtenerPorId(id);

            usuarioService.eliminar(id);

            // Expirar todas las sesiones activas de ese username
            if (usuario != null && usuario.getUsername() != null) {
                String username = usuario.getUsername();
                for (Object principal : sessionRegistry.getAllPrincipals()) {
                    if (principal instanceof UserDetails userDetails && userDetails.getUsername().equals(username)) {
                        for (SessionInformation info : sessionRegistry.getAllSessions(principal, false)) {
                            info.expireNow();
                        }
                    }
                }
            }

            redirectAttributes.addFlashAttribute("mensajeExito", "Usuario dado de baja correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se pudo dar de baja el usuario. " + e.getMessage());
        }
        return "redirect:/usuario";
    }

    @GetMapping("/cambiar-password")
    public String mostrarFormularioCambiarPassword(Model model) {
        model.addAttribute("passwordActual", "");
        model.addAttribute("passwordNuevo", "");
        model.addAttribute("confirmarPasswordNuevo", "");
        return "Usuario/CambiarPassword";
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@RequestParam("passwordActual") String passwordActual,
            @RequestParam("passwordNuevo") String passwordNuevo,
            @RequestParam("confirmarPasswordNuevo") String confirmarPasswordNuevo,
            Model model, RedirectAttributes redirectAttributes) {

        if (!passwordNuevo.equals(confirmarPasswordNuevo)) {
            model.addAttribute("mensajeError", "La nueva contraseña y su confirmación no coinciden.");
            model.addAttribute("passwordActual", "");
            model.addAttribute("passwordNuevo", "");
            model.addAttribute("confirmarPasswordNuevo", "");
            return "Usuario/CambiarPassword";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : null;
        if (username == null) {
            redirectAttributes.addFlashAttribute("mensajeError",
                    "No se pudo identificar al usuario actual. Inicie sesión nuevamente.");
            return "redirect:/login";
        }

        try {
            usuarioService.cambiarPassword(username, passwordActual, passwordNuevo);
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Contraseña actualizada correctamente. Vuelva a iniciar sesión con su nueva clave.");
            return "redirect:/logout";
        } catch (Exception e) {
            model.addAttribute("mensajeError", e.getMessage() != null ? e.getMessage()
                    : "No se pudo actualizar la contraseña.");
            model.addAttribute("passwordActual", "");
            model.addAttribute("passwordNuevo", "");
            model.addAttribute("confirmarPasswordNuevo", "");
            return "Usuario/CambiarPassword";
        }
    }
}
