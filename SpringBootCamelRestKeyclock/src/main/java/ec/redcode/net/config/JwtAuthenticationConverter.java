package ec.redcode.net.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter JWT_GRANTED_AUTHORITIES_CONVERTER = new JwtGrantedAuthoritiesConverter();

    @Value("${jwt.auth.converter.principal-atribute}")
    private String principalAttribute;

    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream
                .concat(JWT_GRANTED_AUTHORITIES_CONVERTER.convert(jwt).stream(), extractResourceRole(jwt).stream())
                .toList();
        return new JwtAuthenticationToken(jwt, authorities, getPrincipalName(jwt));
    }

    private String getPrincipalName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;
        if (null != principalAttribute) {
            claimName = principalAttribute;
        }
        return jwt.getClaim(claimName);
    }

    private Collection<? extends GrantedAuthority> extractResourceRole(Jwt jwt) {
        Map<String, Object> resourceAccess;
        Map<String, Object> resource;
        Collection<String> resourceRoles;

        if (null == jwt.getClaim("resource_access")) {
            return List.of();
        }
        resourceAccess = jwt.getClaim("resource_access");

        if (null == resourceAccess.get(resourceId)) {
            return List.of();
        }
        resource = (Map<String, Object>) resourceAccess.get(resourceId);

        if (null == resource.get("roles")) {
            return List.of();
        }
        resourceRoles = (Collection<String>) resource.get("roles");

        return resourceRoles
                .stream()
                .map(
                        role -> new SimpleGrantedAuthority("ROLE_".concat(role))).collect(Collectors.toList()
                );
    }
}
