package com.example.foods.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OAuth2LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException, ServletException {

    log.error("OAuth2 authentication failed: {}", exception.getMessage(), exception);

    String errorMessage = "OAuth2 login failed";
    if (exception.getMessage() != null) {
      if (exception.getMessage().contains("Email not found")) {
        errorMessage =
            "Email not found from Google. Please ensure your Google account has a public email.";
      } else if (exception.getMessage().contains("Unable to extract username")) {
        errorMessage = "Unable to get user information from Google. Please try again.";
      } else {
        errorMessage = "Google login failed: Unexpected error occurred.";
      }
    }

    setDefaultFailureUrl("/login?error=oauth2&message=" + errorMessage.replace(" ", "%20"));
    super.onAuthenticationFailure(request, response, exception);
  }
}
