package ec.redcode.net.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> {
                    authorizeRequests.requestMatchers(
                            "/swagger-ui/**",
                            "/api/swagger-ui/**",
                            "/v3/api-docs",
                            "/v3/api-docs/**",
                            "/api-docs/swagger-config",
                            "/api-doc/**",
                            "/api/api-doc/**",
                            "/camel/api/api-doc/**",
                            "/webjars/**",
                            "/favicon.ico",
                            "/api-doc",
                            "/swagger-ui/",
                            "/api/test/public"
                    ).permitAll();
                    authorizeRequests.requestMatchers(HttpMethod.GET, "/camel/api/admin-camel").hasRole("admin_client_role");
                    authorizeRequests.requestMatchers(HttpMethod.GET, "/camel/api/user-camel").hasRole("user_client_role");
                    authorizeRequests.anyRequest().authenticated();
                    //authorizeRequests.requestMatchers("/**").permitAll();
                })
                .oauth2ResourceServer(oauth2 -> {
                    oauth2.jwt(jwt -> {
                        jwt.jwtAuthenticationConverter(jwtAuthenticationConverter);
                    });
                })
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}
