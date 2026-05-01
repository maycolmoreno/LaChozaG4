package com.choza.consumochoza.config;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CustomUserDetails extends User {

	private static final long serialVersionUID = 1L;

	private final boolean requiereCambioPassword;

	public CustomUserDetails(String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities, boolean requiereCambioPassword) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.requiereCambioPassword = requiereCambioPassword;
	}

	public boolean isRequiereCambioPassword() {
		return requiereCambioPassword;
	}
}
