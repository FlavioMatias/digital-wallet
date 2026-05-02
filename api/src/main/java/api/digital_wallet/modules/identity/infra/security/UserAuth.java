package api.digital_wallet.modules.identity.infra.security;

import api.digital_wallet.modules.identity.domain.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UserAuth implements UserDetails {

    private final User user;

    public UserAuth(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if (user.getProfile() == null || user.getProfile().getRole() == null) {
            return Collections.emptyList();
        }


        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getProfile().getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail().address();
    }

}