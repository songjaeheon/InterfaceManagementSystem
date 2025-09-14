package com.ims.eims.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("local") // local 프로필에서만 보안 완화 적용
public class LocalOpenSecurityConfig {

  @Bean
  public SecurityFilterChain openSecurityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())                 // 로컬 테스트 편의
      .authorizeHttpRequests(auth -> auth
        .anyRequest().permitAll()                   // 전면 허용
      )
      .formLogin(form -> form.disable());

    return http.build();
  }
}
