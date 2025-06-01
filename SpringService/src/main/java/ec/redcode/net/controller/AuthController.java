package ec.redcode.net.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class AuthController {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthController(JwtEncoder jwtEncoder,
                          JwtDecoder jwtDecoder,
                          AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> generarToken(@RequestParam String grantType,
                                                            @RequestParam String username,
                                                            @RequestParam String password,
                                                            @RequestParam boolean withRefreshToken,
                                                            @RequestParam String refreshToken) {
        String subject = null;
        String scope = null;

        if ("password".equals(grantType)) {
            //Autenticamos al usuario con sus datos
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            //obtenemos los datos del usuario authenticado
            subject = authentication.getName();
            scope = authentication.getAuthorities()
                    .stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));
        } else if ("refreshToken".equals(grantType)) {
            if (null == refreshToken) {
                return new ResponseEntity<>(Map.of("errorMessage", "El refreshToken es requerido"), HttpStatus.UNAUTHORIZED);
            }
            Jwt decodeJwt = null;
            try {
                //extraemos informacion del decodeJwt
                decodeJwt = jwtDecoder.decode(refreshToken);
            } catch (JwtException e) {
                return new ResponseEntity<>(Map.of("errorMessage", e.getMessage()), HttpStatus.UNAUTHORIZED);
            }
            subject = decodeJwt.getSubject();
            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            scope = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));
        }

        Map<String, String> idToken = new HashMap<>();
        Instant now = Instant.now(); // obtener hora actual

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(subject)
                .issuedAt(now) //Establece la hora de emision del token
                .expiresAt(now.plus(withRefreshToken ? 1 : 5, ChronoUnit.MINUTES)) //Establece la fecha de expiracion del token, 5 min despues
                .issuer("security-service") // Emisor del token
                .claim("scope", scope)
                .build();

        String jwtAccessToken = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
        idToken.put("accessToken", jwtAccessToken);

        if (withRefreshToken) {
            JwtClaimsSet jwtClaimsSetRefresh = JwtClaimsSet.builder()
                    .subject(subject)
                    .issuedAt(now) //Establece la hora de emision del token
                    .expiresAt(now.plus(5, ChronoUnit.MINUTES)) //Establece la fecha de expiracion del token, 5 min despues
                    .issuer("security-service") // Emisor del token
                    .claim("scope", scope)
                    .build();
            String jwtAccessTokenRefresh = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSetRefresh)).getTokenValue();
            idToken.put("refreshToken", jwtAccessTokenRefresh);
        }
        return new ResponseEntity<>(idToken, HttpStatus.OK);
    }

}
