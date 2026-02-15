package com.choza.consumochoza.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.choza.consumochoza.modelo.dto.LoginResponseDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * AuthenticationProvider que delega la autenticación a la API REST (pisip).
 * Llama a POST /api/usuarios/login y obtiene un JWT token.
 * El token se almacena en la HttpSession para ser usado en llamadas posteriores.
 */
@Component
public class ApiAuthenticationProvider implements AuthenticationProvider {

    private final WebClient loginWebClient;
    private final HttpServletRequest request;

    public ApiAuthenticationProvider(@Value("${api.base-url}") String apiBaseUrl,
                                    HttpServletRequest request) {
        // WebClient independiente sin filtro de JWT (el login es público)
        this.loginWebClient = WebClient.builder()
                .baseUrl(apiBaseUrl)
                .build();
        this.request = request;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        try {
            // Llamar al endpoint de login de la API
            LoginResponseDTO loginResponse = loginWebClient.post()
                    .uri("/usuarios/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new LoginRequest(username, password))
                    .retrieve()
                    .bodyToMono(LoginResponseDTO.class)
                    .block();

            if (loginResponse == null || loginResponse.getToken() == null) {
                throw new BadCredentialsException("Credenciales inválidas");
            }

            // Guardar el JWT en la sesión HTTP
            HttpSession session = request.getSession(true);
            session.setAttribute("JWT_TOKEN", loginResponse.getToken());

            // Crear el CustomUserDetails con la info del usuario
            CustomUserDetails userDetails = new CustomUserDetails(
                    loginResponse.getUsername(),
                    "", // No necesitamos la contraseña
                    true, true, true, true,
                    Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_" + loginResponse.getRol().toUpperCase())
                    ),
                    loginResponse.isRequiereCambioPassword()
            );

            return new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );

        } catch (WebClientResponseException.Unauthorized e) {
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        } catch (WebClientResponseException e) {
            throw new BadCredentialsException("Error al autenticar: " + e.getMessage());
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new BadCredentialsException("Error de comunicación con la API: " + e.getMessage());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * DTO interno para enviar las credenciales al API.
     */
    private record LoginRequest(String username, String password) {}
}
