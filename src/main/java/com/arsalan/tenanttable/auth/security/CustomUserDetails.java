package com.arsalan.tenanttable.auth.security;

import com.arsalan.tenanttable.user.entity.User;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (user.getPlatformRole() != null) {
            authorities.add(
                    new SimpleGrantedAuthority("ROLE_" + user.getPlatformRole().name())
            );
        }

        if (user.getTenantRole() != null) {
            authorities.add(
                    new SimpleGrantedAuthority("ROLE_" + user.getTenantRole().name())
            );
        }

        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public @NullMarked String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }

}
