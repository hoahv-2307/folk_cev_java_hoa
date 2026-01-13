package com.example.foods.config;

import com.example.foods.entity.User;
import com.example.foods.repository.UserRepository;
import com.example.foods.service.CustomOidcUser;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOidcUserService extends OidcUserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
    try {
      OidcUser oidcUser = super.loadUser(userRequest);
      log.info("OIDC User Attributes: {}", oidcUser.getAttributes());
      return processOidcUser(oidcUser);
    } catch (Exception ex) {
      log.error("Error processing OIDC user", ex);

      // If there's a sub attribute error, try to handle it manually
      if (ex.getMessage() != null && ex.getMessage().contains("sub")) {
        log.warn("Handling sub attribute error manually");
        return handleSubError(userRequest);
      }

      throw new OAuth2AuthenticationException("Error processing OIDC user: " + ex.getMessage());
    }
  }

  private OidcUser handleSubError(OidcUserRequest userRequest) {
    // Get user info directly from the access token
    Map<String, Object> attributes = new HashMap<>();

    // Add some default values
    String email = (String) userRequest.getIdToken().getClaims().get("email");
    String name = (String) userRequest.getIdToken().getClaims().get("name");
    String sub = (String) userRequest.getIdToken().getClaims().get("sub");

    if (email != null)
      attributes.put("email", email);
    if (name != null)
      attributes.put("name", name);
    if (sub != null) {
      attributes.put("sub", sub);
    } else {
      // Use email as fallback for sub
      attributes.put("sub", email != null ? email : "unknown");
    }

    log.info("Manually created attributes: {}", attributes);

    User user = findOrCreateUser(attributes);
    return new CustomOidcUser(user, attributes, userRequest.getIdToken());
  }

  private OidcUser processOidcUser(OidcUser oidcUser) {
    String email = oidcUser.getAttribute("email");

    if (email == null || email.trim().isEmpty()) {
      throw new OAuth2AuthenticationException("Email not found from OIDC provider");
    }

    Map<String, Object> attributes = new HashMap<>(oidcUser.getAttributes());
    if (!attributes.containsKey("sub") || attributes.get("sub") == null) {
      String googleId = oidcUser.getAttribute("id");
      attributes.put("sub", googleId != null ? googleId : email);
    }

    User user = findOrCreateUser(attributes);
    return new CustomOidcUser(user, attributes, oidcUser.getIdToken());
  }

  private User findOrCreateUser(Map<String, Object> attributes) {
    String email = (String) attributes.get("email");

    User user = userRepository.findByEmail(email).orElse(null);

    if (user == null) {
      user = createNewUser(attributes);
    } else {
      log.info("Existing OIDC user logged in with email: {}", user.getEmail());
    }

    return user;
  }

  private User createNewUser(Map<String, Object> attributes) {
    String email = (String) attributes.get("email");
    String givenName = (String) attributes.get("given_name");

    String username;
    if (givenName != null && !givenName.trim().isEmpty()) {
      username = givenName.toLowerCase().trim();
    } else if (email != null && email.contains("@")) {
      username = email.substring(0, email.indexOf("@")).toLowerCase();
    } else {
      throw new OAuth2AuthenticationException("Unable to extract username from OIDC user data");
    }

    // Ensure username is unique
    String originalUsername = username;
    int counter = 1;
    while (userRepository.findByUsername(username).isPresent()) {
      username = originalUsername + counter;
      counter++;
    }

    User user = User.builder()
        .username(username)
        .email(email)
        .password(passwordEncoder.encode("oauth2_user"))
        .role("USER")
        .build();

    user = userRepository.save(user);
    log.info("New OIDC user created with email: {}", email);

    return user;
  }
}
