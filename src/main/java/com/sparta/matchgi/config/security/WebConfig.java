package com.sparta.matchgi.config.security;

import com.sparta.matchgi.auth.FilterSkipMatcher;
import com.sparta.matchgi.auth.FormLoginFailureHandler;
import com.sparta.matchgi.auth.FormLoginSuccessHandler;
import com.sparta.matchgi.auth.filter.FormLoginFilter;
import com.sparta.matchgi.auth.filter.JwtAuthFilter;
import com.sparta.matchgi.auth.jwt.HeaderTokenExtractor;
import com.sparta.matchgi.auth.jwt.JwtDecoder;
import com.sparta.matchgi.auth.oauth2.OAuth2AuthenticationFailureHandler;
import com.sparta.matchgi.auth.oauth2.OAuth2AuthenticationSuccessHandler;
import com.sparta.matchgi.auth.oauth2.UserOAuth2Service;
import com.sparta.matchgi.auth.provider.FormLoginAuthProvider;
import com.sparta.matchgi.auth.provider.JWTAuthProvider;
import com.sparta.matchgi.repository.RefreshTokenRepository;
import com.sparta.matchgi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebConfig extends WebSecurityConfigurerAdapter {

    private final JWTAuthProvider jwtAuthProvider;
    private final HeaderTokenExtractor headerTokenExtractor;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtDecoder jwtDecoder;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final UserRepository userRepository;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public FormLoginAuthProvider formLoginAuthProvider() {
        return new FormLoginAuthProvider(passwordEncoder());
    }

    @Bean
    public FormLoginSuccessHandler formLoginSuccessHandler() {
        return new FormLoginSuccessHandler(refreshTokenRepository);
    }

    @Bean
    public FormLoginFailureHandler formLoginFailureHandler() {
        return new FormLoginFailureHandler();
    }

    @Bean
    public UserOAuth2Service userOAuth2Service(){
        return new UserOAuth2Service(userRepository,passwordEncoder());
    }

    @Bean
    public FormLoginFilter formLoginFilter() throws Exception {
        FormLoginFilter formLoginFilter = new FormLoginFilter(authenticationManager());
        formLoginFilter.setFilterProcessesUrl("/api/signin");
        formLoginFilter.setAuthenticationSuccessHandler(formLoginSuccessHandler());
        formLoginFilter.setAuthenticationFailureHandler(formLoginFailureHandler());
        formLoginFilter.afterPropertiesSet();
        return formLoginFilter;
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(formLoginAuthProvider())
                .authenticationProvider(jwtAuthProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors();
        http
                .addFilterBefore(formLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
        http
                .oauth2Login()
                .defaultSuccessUrl("/")
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
                .userInfoEndpoint()
                .userService(userOAuth2Service());
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .logout()
                .logoutUrl("/api/logout")
                .permitAll()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST,"/api/signup","/api/signin","/api/refresh").permitAll()
                .antMatchers("/ws-stomp").permitAll()
                .antMatchers("/ws-stomp/**").permitAll()
                .antMatchers(HttpMethod.GET,"/api/users/rank","/api/posts").permitAll()
                .anyRequest().permitAll();
    }

    private JwtAuthFilter jwtFilter() throws Exception {
        List<String> skipPathList = new ArrayList<>();

        skipPathList.add("POST,/api/signin");
        skipPathList.add("POST,/api/signup");
        skipPathList.add("GET,/");
        skipPathList.add("GET,/api/room/*");
        skipPathList.add("GET,/api/rooms");
        skipPathList.add("PUT,/api/refresh");
        skipPathList.add("GET,/ws-stomp");
        skipPathList.add("GET,/ws-stomp/**");
        skipPathList.add("POST,/ws-stomp/**");
        skipPathList.add("POST,/ws-stomp");
        skipPathList.add("GET,/api/users/rank");
        skipPathList.add("GET,/api/posts");

        FilterSkipMatcher matcher = new FilterSkipMatcher(
                skipPathList,
                "/**"
        );

        JwtAuthFilter filter = new JwtAuthFilter(matcher, headerTokenExtractor,jwtDecoder);
        filter.setAuthenticationManager(super.authenticationManagerBean());

        return filter;
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
}