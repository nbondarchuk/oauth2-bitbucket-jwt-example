package com.nbondarchuk.oauth2.server.security;

import com.nbondarchuk.oauth2.server.model.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author Nikolay Bondarchuk
 * @since 2020-11-07
 */
public class ExampleOAuth2User implements OAuth2User, UserDetails {

    private final Long id;

    // user login
    private final String name;

    private final String fullName;

    private final String provider;

    private final Map<String, Object> attributes;

    private final Set<GrantedAuthority> authorities;

    public ExampleOAuth2User(User user) {
        this(user, emptyMap());
    }

    public ExampleOAuth2User(User user, Map<String, Object> attributes) {
        checkArgument(user.getId() != null, "id cannot be null");
        checkArgument(isNotBlank(user.getName()), "name cannot ne empty");

        this.id = user.getId();
        this.name = user.getLogin();
        this.fullName = user.getName();
        this.provider = user.getProvider();
        this.attributes = unmodifiableMap(new LinkedHashMap<>(attributes));
        this.authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public Long getId() {
        return id;
    }

    public String getProvider() {
        return provider;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUsername() {
        return getName();
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ExampleOAuth2User that = (ExampleOAuth2User) obj;

        if (!getId().equals(that.getId())) {
            return false;
        }
        if (!getName().equals(that.getName())) {
            return false;
        }
        if (!getAuthorities().equals(that.getAuthorities())) {
            return false;
        }
        return this.getAttributes().equals(that.getAttributes());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getAuthorities().hashCode();
        result = 31 * result + getAttributes().hashCode();
        return result;
    }

    @Override
    @SuppressWarnings("StringBufferReplaceableByString")
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Id: [");
        sb.append(getId());
        sb.append("], Name: [");
        sb.append(getName());
        sb.append("], Granted Authorities: [");
        sb.append(getAuthorities());
        sb.append("], User Attributes: [");
        sb.append(getAttributes());
        sb.append("]");
        return sb.toString();
    }
}
