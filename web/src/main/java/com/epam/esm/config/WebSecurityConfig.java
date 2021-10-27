package com.epam.esm.config;

import com.epam.esm.security.JwtConfigurer;
import com.epam.esm.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;

    private static final String LOGIN_ENDPOINT = "/user/login";
    private static final String USER = "USER";
    private static final String ADMIN = "ADMIN";

    @Autowired
    public WebSecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(LOGIN_ENDPOINT).permitAll()
                .antMatchers(HttpMethod.POST, "/user").permitAll()
                .antMatchers(HttpMethod.GET, "/gift").permitAll()
                .antMatchers(HttpMethod.POST, "/userOrder").hasRole(USER)
                .antMatchers(HttpMethod.GET, "/tag", "/user", "/userOrder").hasRole(USER)
                .antMatchers(HttpMethod.POST, "/gift", "/tag").hasRole(ADMIN)
                .antMatchers(HttpMethod.DELETE, "/gift", "/tag", "/user", "/userOrder").hasRole(ADMIN)
                .antMatchers(HttpMethod.PATCH, "/gift").hasRole(ADMIN)
                .antMatchers(HttpMethod.PUT, "/gift").hasRole(ADMIN)
                .anyRequest().authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
    }
}
