package com.lachozag4.pisip.infraestructura.configuracion;

import com.lachozag4.pisip.infraestructura.seguridad.JwtAccessDeniedHandler;
import com.lachozag4.pisip.infraestructura.seguridad.JwtAuthenticationEntryPoint;
import com.lachozag4.pisip.infraestructura.seguridad.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SeguridadConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final String corsAllowedOrigins;

    public SeguridadConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                           JwtAuthenticationEntryPoint authenticationEntryPoint,
                           JwtAccessDeniedHandler accessDeniedHandler,
                           @Value("${cors.allowed-origins:http://localhost:8085}") String corsAllowedOrigins) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.corsAllowedOrigins = corsAllowedOrigins;
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

                // ═══════════════════════════════════════════════════════════════
                // ENDPOINTS PÚBLICOS
                // ═══════════════════════════════════════════════════════════════
                .requestMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/usuarios/existe-alguno").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios/setup-admin").permitAll()
                .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()

                // ═══════════════════════════════════════════════════════════════
                // WEBSOCKET — el handshake HTTP debe pasar sin filtro JWT
                // La autorización real se hace en el STOMP CONNECT (o con JWT en header)
                // ═══════════════════════════════════════════════════════════════
                .requestMatchers("/ws/**", "/ws-sockjs/**").permitAll()

                // ═══════════════════════════════════════════════════════════════
                // SWAGGER / OPENAPI — solo ADMIN
                // ═══════════════════════════════════════════════════════════════
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html",
                                 "/v3/api-docs/**", "/v3/api-docs").hasRole("ADMIN")

                // ═══════════════════════════════════════════════════════════════
                // USUARIOS
                // ═══════════════════════════════════════════════════════════════
                // Perfil propio: cualquier rol autenticado puede consultar su usuario
                .requestMatchers(HttpMethod.GET, "/api/usuarios/por-username/**").authenticated()
                // Cambio de contraseña: cualquier usuario autenticado
                .requestMatchers(HttpMethod.POST, "/api/usuarios/cambiar-password").authenticated()
                // Resto de usuarios: solo ADMIN
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                // ═══════════════════════════════════════════════════════════════
                // CATEGORÍAS — lectura: todos los roles | escritura: ADMIN
                // ═══════════════════════════════════════════════════════════════
                .requestMatchers(HttpMethod.GET, "/api/categorias/**")
                    .hasAnyRole("ADMIN", "CAMARERO", "COCINA", "CAJERO")
                .requestMatchers(HttpMethod.POST,   "/api/categorias/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/categorias/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/categorias/**").hasRole("ADMIN")

                // ═══════════════════════════════════════════════════════════════
                // PRODUCTOS — lectura: todos los roles | escritura: ADMIN
                // ═══════════════════════════════════════════════════════════════
                .requestMatchers(HttpMethod.GET, "/api/productos/**")
                    .hasAnyRole("ADMIN", "CAMARERO", "COCINA", "CAJERO")
                .requestMatchers(HttpMethod.POST,   "/api/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")

                // ═══════════════════════════════════════════════════════════════
                // MESAS — lectura: ADMIN/CAMARERO/CAJERO | escritura: ADMIN
                // ═══════════════════════════════════════════════════════════════
                .requestMatchers(HttpMethod.GET, "/api/mesas/**")
                    .hasAnyRole("ADMIN", "CAMARERO", "CAJERO")
                .requestMatchers(HttpMethod.POST,   "/api/mesas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/mesas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/mesas/**").hasRole("ADMIN")

                // ═══════════════════════════════════════════════════════════════
                // CLIENTES
                //   GET   → ADMIN/CAMARERO/CAJERO
                //   POST  → ADMIN/CAMARERO/CAJERO  (crear cliente en sala o en caja)
                //   PUT   → ADMIN                  (actualización completa de datos)
                //   DELETE→ ADMIN
                // ═══════════════════════════════════════════════════════════════
                .requestMatchers(HttpMethod.GET,  "/api/clientes/**")
                    .hasAnyRole("ADMIN", "CAMARERO", "CAJERO")
                .requestMatchers(HttpMethod.POST, "/api/clientes/**")
                    .hasAnyRole("ADMIN", "CAMARERO", "CAJERO")
                .requestMatchers(HttpMethod.PUT,    "/api/clientes/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/clientes/**").hasRole("ADMIN")

                // ═══════════════════════════════════════════════════════════════
                // PEDIDOS — acceso base URL-level; control fino vía @PreAuthorize
                //   GET    → ADMIN/CAMARERO/COCINA/CAJERO
                //   POST   → ADMIN/CAMARERO
                //   PUT    → ADMIN/CAMARERO
                //   DELETE → ADMIN/CAMARERO
                //   PATCH  → se delega a @PreAuthorize por semántica de estado
                // ═══════════════════════════════════════════════════════════════
                .requestMatchers(HttpMethod.GET, "/api/pedidos/**")
                    .hasAnyRole("ADMIN", "CAMARERO", "COCINA", "CAJERO")
                .requestMatchers(HttpMethod.POST,   "/api/pedidos/**")
                    .hasAnyRole("ADMIN", "CAMARERO")
                .requestMatchers(HttpMethod.PUT,    "/api/pedidos/**")
                    .hasAnyRole("ADMIN", "CAMARERO")
                .requestMatchers(HttpMethod.DELETE, "/api/pedidos/**")
                    .hasAnyRole("ADMIN", "CAMARERO")
                .requestMatchers(HttpMethod.PATCH,  "/api/pedidos/**")
                    .hasAnyRole("ADMIN", "CAMARERO", "COCINA", "CAJERO")

                // ═══════════════════════════════════════════════════════════════
                // CUENTAS (nota: /api/cuentas/** cubre también los pagos anidados)
                //   GET    → ADMIN/CAMARERO/CAJERO
                //   POST   → ADMIN/CAMARERO/CAJERO (crear cuenta, agregar pedido a cuenta)
                //   PATCH  → ADMIN/CAJERO          (cambiar estado = cobrar)
                // ═══════════════════════════════════════════════════════════════
                .requestMatchers(HttpMethod.GET,   "/api/cuentas/**")
                    .hasAnyRole("ADMIN", "CAMARERO", "CAJERO")
                .requestMatchers(HttpMethod.POST,  "/api/cuentas/**")
                    .hasAnyRole("ADMIN", "CAMARERO", "CAJERO")
                .requestMatchers(HttpMethod.PATCH, "/api/cuentas/**")
                    .hasAnyRole("ADMIN", "CAJERO")

                // ═══════════════════════════════════════════════════════════════
                // CAJA — solo ADMIN/CAJERO
                // ═══════════════════════════════════════════════════════════════
                .requestMatchers("/api/caja/**").hasAnyRole("ADMIN", "CAJERO")

                // ═══════════════════════════════════════════════════════════════
                // REPORTES — solo ADMIN
                // ═══════════════════════════════════════════════════════════════
                .requestMatchers("/api/reportes/**").hasRole("ADMIN")

                // ═══════════════════════════════════════════════════════════════
                // COMEDOR — solo ADMIN (gestión de salones)
                // ═══════════════════════════════════════════════════════════════
                .requestMatchers("/api/comedores/**").hasRole("ADMIN")

                // ═══════════════════════════════════════════════════════════════
                // CUALQUIER OTRO ENDPOINT — requiere autenticación
                // ═══════════════════════════════════════════════════════════════
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
        List<String> origins = Arrays.stream(corsAllowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);
        return new UrlBasedCorsConfigurationSource() {{
            registerCorsConfiguration("/**", config);
        }};
    }
}
