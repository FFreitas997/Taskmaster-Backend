package com.ffreitas.taskmaster.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Value("${spring.security.oauth2.resourceserver.jwt.principal-name}")
    private String principalName;

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt source) {
        Set<GrantedAuthority> authorities = Stream.concat(
                new JwtGrantedAuthoritiesConverter().convert(source).stream(),
                extractResourceRoles(source).stream()
        ).collect(Collectors.toSet());
        return new JwtAuthenticationToken(source, authorities, getPrincipalClaimName(source));
    }

    private String getPrincipalClaimName(Jwt source) {
        return source.getClaim(principalName);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt source) {
        HashMap<String, Object> resourceAccess = new HashMap<>(source.getClaim("realm_access"));

        ArrayList<String> roles = (ArrayList<String>) resourceAccess.get("roles");

        return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.replace("-", "_"))).collect(Collectors.toSet());
    }
}
