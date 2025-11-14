package com.example.hello_friends.security;

import com.example.hello_friends.security.filter.JwtFilter;
import com.example.hello_friends.security.handler.AccessDeniedCatcher;
import com.example.hello_friends.security.userdetail.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@Profile("prod")
@EnableMethodSecurity(securedEnabled = true)
@EnableTransactionManagement(order = 2)
@RequiredArgsConstructor
public class SecurityConfigProd {
    private final JwtFilter jwtFilterProd;

    private final AccessDeniedCatcher accessDeniedCatcher;
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception{
        http.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(Customizer.withDefaults())
                .csrf(csrfConfig->csrfConfig.disable())
                .addFilterBefore(jwtFilterProd, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth-> auth
                        .requestMatchers("/api/auth/**","/swagger-ui/**","/v3/api-docs/**","/api/user/register").permitAll()
                        .requestMatchers("/api/admin/**").hasAnyRole(Role.ADMIN.name())
                        .requestMatchers("/api/user/**").hasAnyRole(Role.ADMIN.name(),Role.SELLER.name(),Role.CUSTOMER.name())
                        .requestMatchers("/api/**").authenticated());


        http.exceptionHandling(exh->exh.accessDeniedHandler(accessDeniedCatcher));
        http.formLogin(fl->fl.disable());
        http.logout(lo->lo.disable());
        http.httpBasic(hbc -> hbc.disable());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider ){
        return new ProviderManager(authenticationProvider);
    }

    // 삭제해야됨 test용
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
