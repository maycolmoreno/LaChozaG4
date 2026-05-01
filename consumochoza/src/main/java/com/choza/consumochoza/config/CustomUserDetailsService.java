package com.choza.consumochoza.config;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.choza.consumochoza.modelo.dto.UsuarioDTO;
import com.choza.consumochoza.service.IUsuarioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final IUsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar todos los usuarios y filtrar por username
        UsuarioDTO usuario = usuarioService.listarTodos().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        if (!usuario.isEstado()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }

        return new CustomUserDetails(
                usuario.getUsername(),
                usuario.getPassword() != null ? usuario.getPassword() : "",
                true,
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toUpperCase())),
                usuario.isRequiereCambioPassword()
        );
    }
}
