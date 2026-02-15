	package com.choza.consumochoza.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final ApiAuthenticationProvider apiAuthenticationProvider;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth
				// Recursos públicos
				.requestMatchers("/login", "/setup-admin", "/css/**", "/js/**", "/assets/**", "/images/**", "/dist/**",
						"/manifest.json", "/sw.js", "/offline.html")
				.permitAll()

				// === RUTAS POR ROL ===

				// API del POS (para clientes, cuentas, pedidos activos)
				.requestMatchers("/api/pos/**").hasAnyRole("CAMARERO", "ADMIN")

				// COCINA: ve pedidos pendientes (ADMIN también puede acceder)
				.requestMatchers("/cocina/**", "/producto/**").hasAnyRole("COCINA", "ADMIN")

				// CAMARERO: solo acceso al POS y pedidos
				.requestMatchers("/pedidos/**").hasAnyRole("CAMARERO", "ADMIN")

				// ADMIN: acceso total a configuración
				.requestMatchers("/usuario/**", "/categoria/**", "/producto/**", "/mesa/**", "/cliente/**")
				.hasRole("ADMIN")

				// Todo lo demás requiere autenticación
				.anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login")
						.successHandler(authenticationSuccessHandler()).failureUrl("/login?error=true")
						.usernameParameter("username").passwordParameter("password").permitAll())
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout=true")
						.invalidateHttpSession(true).deleteCookies("JSESSIONID").permitAll())
				.exceptionHandling(ex -> ex.accessDeniedPage("/acceso-denegado"))
				.sessionManagement(session -> session.maximumSessions(-1)
						.sessionRegistry(sessionRegistry()));

		return http.build();
	}

	@Bean
	AuthenticationSuccessHandler authenticationSuccessHandler() {
		return (request, response, authentication) -> {
			Object principal = authentication.getPrincipal();
			boolean esAdmin = authentication.getAuthorities().stream()
					.anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
			if (esAdmin && principal instanceof CustomUserDetails customUser
					&& customUser.isRequiereCambioPassword()) {
				response.sendRedirect(request.getContextPath() + "/usuario/cambiar-password");
			} else {
				response.sendRedirect(request.getContextPath() + "/");
			}
		};
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager() {
		return new ProviderManager(apiAuthenticationProvider);
	}

	@Bean
	SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	static HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}
}
