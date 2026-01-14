package com.example.foods.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  @Value("${app.csrf.enabled:true}")
  private boolean csrfEnabled;

  private final OidcUserService customOidcUserService;
  private final OAuth2LoginFailureHandler oauth2LoginFailureHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(
            csrf -> {
              if (!csrfEnabled) {
                csrf.disable();
              }
            })
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/login", "/register", "/css/**", "/js/**", "/images/**")
                    .permitAll()
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated())
        .oauth2Login(
            oauth2 ->
                oauth2
                    .loginPage("/login")
                    .defaultSuccessUrl("/foods", true)
                    .failureHandler(oauth2LoginFailureHandler)
                    .userInfoEndpoint(
                        userInfo -> {
                          userInfo.oidcUserService(customOidcUserService);
                        })
                    .permitAll())
        .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/foods", true).permitAll())
        .logout(logout -> logout.permitAll());

    return http.build();
  }
}
