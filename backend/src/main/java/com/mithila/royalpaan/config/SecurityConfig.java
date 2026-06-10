package com.mithila.royalpaan.config;

import com.mithila.royalpaan.security.JwtAuthenticationFilter;
import com.mithila.royalpaan.security.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless REST API
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // Needed for OAuth2 flow
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json");
                    response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                    response.getOutputStream().println("{ \"error\": \"Unauthorized\", \"message\": \"" + authException.getMessage() + "\" }");
                })
            )
            .authorizeHttpRequests(auth -> auth
                // Static resources & HTML pages
                .requestMatchers("/", "/index.html", "/about.html", "/services.html", 
                               "/wedding-paan.html", "/event-paan.html", "/products.html", 
                               "/gallery.html", "/blog.html", "/contact.html", 
                               "/login.html", "/register.html", "/dashboard.html", "/admin.html",
                               "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                
                // Public APIs
                .requestMatchers("/api/v1/auth/login", "/api/v1/auth/register", "/api/v1/auth/forgot-password", "/api/v1/auth/verify-email").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/settings").permitAll()    // CMS settings must be public for all pages
                .requestMatchers(HttpMethod.GET, "/api/v1/settings/**").permitAll() // CMS settings read - public
                .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/blogs/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/gallery/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/testimonials/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/faqs/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/services/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/contact").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/export-enquiries").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/chat/message").permitAll()  // Public chatbot
                
                // Protected APIs (handled by method-level security or custom check)
                .requestMatchers("/api/v1/bookings/**").authenticated()
                .requestMatchers("/api/v1/orders/**").authenticated()
                .requestMatchers("/api/v1/notifications/**").authenticated()
                .requestMatchers("/api/v1/users/profile").authenticated()
                
                // Admin statistics & configurations
                .requestMatchers("/api/v1/dashboard/stats").hasAnyRole("SUPER_ADMIN", "STAFF")
                
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login.html")
                .successHandler(oAuth2LoginSuccessHandler)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
