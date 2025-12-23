package com.ims.eims.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Security Configuration.
 *
 * <p>
 * Look at this! We're extending {@code WebSecurityConfigurerAdapter} to customize our security settings.
 * For this PoC, we're permitting all requests. It's not production-ready, but it's perfect for
 * getting us off the ground quickly!
 * </p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .anyRequest().permitAll() // Allow everything! Bootifully simple for now.
                .and()
            .csrf().disable() // Disabling CSRF for simplicity in this demo.
            .headers().frameOptions().disable(); // Needed for H2 console to display correctly.
    }
}
