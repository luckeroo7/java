package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Отключаем CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // Разрешаем H2 консоль (новый синтаксис)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))

                // Настройка правил доступа
                .authorizeHttpRequests(auth -> auth
                        // H2 консоль доступна всем
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                        // Публичные страницы
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/public/**")).permitAll()

                        // GET запросы к users доступны всем
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/users")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/users/search")).permitAll()
                        // POST, PUT, DELETE требуют авторизации
                        .anyRequest().authenticated()
                )
                // HTTP Basic аутентификация
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}