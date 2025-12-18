package com.lernia.auth.oauth2;

import com.lernia.auth.entity.UserEntity;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.IOException;
import java.util.List;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final SecurityContextRepository securityContextRepository;

  @Value("${app.frontend.url}")
  private String frontendUrl;

  public OAuth2AuthenticationSuccessHandler(SecurityContextRepository securityContextRepository) {
    this.securityContextRepository = securityContextRepository;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    if (authentication.getPrincipal() instanceof CustomOAuth2User customOAuth2User) {
      UserEntity user = customOAuth2User.getUser();

      // Create new authentication with username
      SecurityContext context = SecurityContextHolder.createEmptyContext();
      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
          user.getUsername(),
          null,
          List.of(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name())));
      context.setAuthentication(authToken);
      SecurityContextHolder.setContext(context);

      // Save the security context
      securityContextRepository.saveContext(context, request, response);
    }

    // Redirect to frontend callback
    getRedirectStrategy().sendRedirect(request, response, frontendUrl + "/oauth/callback");
  }
}
