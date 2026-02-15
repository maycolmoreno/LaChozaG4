package com.lachozag4.pisip.infraestructura.configuracion;

import com.lachozag4.pisip.infraestructura.seguridad.JwtAccessDeniedHandler;
import com.lachozag4.pisip.infraestructura.seguridad.JwtAuthenticationEntryPoint;
import com.lachozag4.pisip.infraestructura.seguridad.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SeguridadConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;

    public SeguridadConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                           JwtAuthenticationEntryPoint authenticationEntryPoint,
                           JwtAccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // --- Endpoints públicos (login, cambio de password, setup) ---
                .requestMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios/cambiar-password").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/usuarios/existe-alguno").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios/setup-admin").permitAll()

                // --- Usuarios: solo ADMIN ---
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                // --- Categorías: ADMIN puede todo, los demás solo lectura ---
                .requestMatchers(HttpMethod.GET, "/api/categorias/**").hasAnyRole("ADMIN", "CAMARERO", "COCINA")
                .requestMatchers("/api/categorias/**").hasRole("ADMIN")

                // --- Productos: ADMIN puede todo, COCINA y CAMARERO solo lectura ---
                .requestMatchers(HttpMethod.GET, "/api/productos/**").hasAnyRole("ADMIN", "CAMARERO", "COCINA")
                .requestMatchers("/api/productos/**").hasRole("ADMIN")

                // --- Mesas: ADMIN puede todo, CAMARERO lectura ---
                .requestMatchers(HttpMethod.GET, "/api/mesas/**").hasAnyRole("ADMIN", "CAMARERO")
                .requestMatchers("/api/mesas/**").hasRole("ADMIN")

                // --- Clientes: ADMIN y CAMARERO ---
                .requestMatchers("/api/clientes/**").hasAnyRole("ADMIN", "CAMARERO")

                // --- Pedidos: ADMIN, CAMARERO y COCINA ---
                .requestMatchers("/api/pedidos/**").hasAnyRole("ADMIN", "CAMARERO", "COCINA")

                // --- Cuentas: ADMIN y CAMARERO ---
                .requestMatchers("/api/cuentas/**").hasAnyRole("ADMIN", "CAMARERO")

                // --- Reportes: solo ADMIN ---
                .requestMatchers("/api/reportes/**").hasRole("ADMIN")

                // --- Cualquier otro endpoint requiere autenticación ---
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:8085"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        return new UrlBasedCorsConfigurationSource() {{
            registerCorsConfiguration("/**", config);
        }};
    }
}
