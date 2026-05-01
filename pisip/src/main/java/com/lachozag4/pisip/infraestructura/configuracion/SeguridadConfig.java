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
                // --- Endpoints públicos (login, cambio de password, setup) ---
                .requestMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios/cambiar-password").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/usuarios/existe-alguno").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios/setup-admin").permitAll()
                .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()
                // --- Swagger / OpenAPI (acceso libre para desarrollo) ---
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs").permitAll()

                // --- Usuarios: perfil propio accesible a cualquier rol autenticado ---
                .requestMatchers(HttpMethod.GET, "/api/usuarios/por-username/**").authenticated()
                // --- Usuarios: resto solo ADMIN ---
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                // --- Categorías: ADMIN puede todo, los demás solo lectura ---
                .requestMatchers(HttpMethod.GET, "/api/categorias/**").hasAnyRole("ADMIN", "CAMARERO", "COCINA", "CAJERO")
                .requestMatchers("/api/categorias/**").hasRole("ADMIN")

                // --- Productos: ADMIN puede todo, COCINA y CAMARERO solo lectura ---
                .requestMatchers(HttpMethod.GET, "/api/productos/**").hasAnyRole("ADMIN", "CAMARERO", "COCINA", "CAJERO")
                .requestMatchers("/api/productos/**").hasRole("ADMIN")

                // --- Mesas: ADMIN puede todo, CAMARERO y CAJERO lectura ---
                .requestMatchers(HttpMethod.GET, "/api/mesas/**").hasAnyRole("ADMIN", "CAMARERO", "CAJERO")
                .requestMatchers("/api/mesas/**").hasRole("ADMIN")

                // --- Clientes: ADMIN, CAMARERO y CAJERO ---
                .requestMatchers("/api/clientes/**").hasAnyRole("ADMIN", "CAMARERO", "CAJERO")

                // --- Pedidos: ADMIN, CAMARERO, COCINA y CAJERO ---
                .requestMatchers("/api/pedidos/**").hasAnyRole("ADMIN", "CAMARERO", "COCINA", "CAJERO")

                // --- Cuentas: ADMIN, CAMARERO y CAJERO ---
                .requestMatchers("/api/cuentas/**").hasAnyRole("ADMIN", "CAMARERO", "CAJERO")

                // --- Pagos: ADMIN, CAMARERO y CAJERO ---
                .requestMatchers("/api/pagos/**").hasAnyRole("ADMIN", "CAMARERO", "CAJERO")

                // --- Caja: ADMIN, CAMARERO y CAJERO ---
                .requestMatchers("/api/caja/**").hasAnyRole("ADMIN", "CAMARERO", "CAJERO")

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
        List<String> origins = Arrays.stream(corsAllowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        config.setAllowedOrigins(origins);
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);
        return new UrlBasedCorsConfigurationSource() {{
            registerCorsConfiguration("/**", config);
        }};
    }
}
