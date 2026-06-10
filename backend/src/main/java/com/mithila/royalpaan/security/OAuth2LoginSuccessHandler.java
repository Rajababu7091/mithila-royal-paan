package com.mithila.royalpaan.security;

import com.mithila.royalpaan.entity.Role;
import com.mithila.royalpaan.entity.User;
import com.mithila.royalpaan.repository.RoleRepository;
import com.mithila.royalpaan.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String googleId = oAuth2User.getAttribute("sub"); // 'sub' is the unique Google user ID

        if (email == null) {
            response.sendRedirect("/login.html?error=oauth_email_missing");
            return;
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // If user exists but has no googleId, update it
            if (user.getGoogleId() == null) {
                user.setGoogleId(googleId);
                user.setProvider("GOOGLE");
                userRepository.save(user);
            }
        } else {
            // Create a new Google user
            user = new User();
            user.setEmail(email);
            user.setName(name != null ? name : "Google User");
            user.setGoogleId(googleId);
            user.setProvider("GOOGLE");
            user.setEmailVerified(true);

            // Assign standard ROLE_CUSTOMER
            Role customerRole = roleRepository.findByName("ROLE_CUSTOMER")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_CUSTOMER")));
            user.setRole(customerRole);

            userRepository.save(user);
        }

        // Generate JWT token
        String jwtToken = jwtUtils.generateTokenFromUsername(email);

        // Redirect customer to the dashboard, providing the token in the URL fragment/query
        String targetUrl = "/dashboard.html?token=" + jwtToken + "&email=" + email;
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
