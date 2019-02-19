package de.propra2.ausleiherino24.model;

import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


public class CustomUserDetails extends User implements UserDetails {

	public CustomUserDetails(final User user) {
		super(user);
	}

	/**
	 * TODO Javadoc
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		ArrayList<SimpleGrantedAuthority> collect = new ArrayList<>();
		collect.add(new SimpleGrantedAuthority("ROLE_" + getRole()));
		return collect;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
