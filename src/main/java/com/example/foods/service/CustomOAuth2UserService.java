package com.example.foods.service;

import com.example.foods.entity.User;
import com.example.foods.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oauth2User = super.loadUser(userRequest);

    System.err.println("OAuth2 User Attributes: " + oauth2User.getAttributes());

    try {
      return processOAuth2User(oauth2User);
    } catch (Exception ex) {
      log.error("Error processing OAuth2 user", ex);
      throw new OAuth2AuthenticationException("Error processing OAuth2 user: " + ex.getMessage());
    }
  }

  private OAuth2User processOAuth2User(OAuth2User oauth2User) {
    String email = oauth2User.getAttribute("email");

    System.err.println("OAuth2 User Email: " + email);

    if (email == null || email.trim().isEmpty()) {
      throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
    }

    Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
    if (!attributes.containsKey("sub") || attributes.get("sub") == null) {
      String googleId = oauth2User.getAttribute("id");
      attributes.put("sub", googleId != null ? googleId : email);
    }

    User user = userRepository.findByEmail(email).orElse(null);

    if (user == null) {
      user = createNewUser(oauth2User);
    } else {
      user = updateExistingUser(user, oauth2User);
    }

    return new CustomOAuth2User(user, attributes);
  }

  private User createNewUser(OAuth2User oauth2User) {
    String email = oauth2User.getAttribute("email");
    String givenName = oauth2User.getAttribute("given_name");

    // Use given name as username, fallback to email prefix if not available
    String username;
    if (givenName != null && !givenName.trim().isEmpty()) {
      username = givenName.toLowerCase().trim();
    } else if (email != null && email.contains("@")) {
      username = email.substring(0, email.indexOf("@")).toLowerCase();
    } else {
      throw new OAuth2AuthenticationException("Unable to extract username from OAuth2 user data");
    }

    // Ensure username is unique
    String originalUsername = username;
    int counter = 1;
    while (userRepository.findByUsername(username).isPresent()) {
      username = originalUsername + counter;
      counter++;
    }

    User user =
        User.builder()
            .username(username)
            .email(email)
            .password(
                passwordEncoder.encode("oauth2_user")) // Placeholder password for OAuth2 users
            .role("USER")
            .build();

    user = userRepository.save(user);
    log.info("New OAuth2 user created with email: {}", email);

    return user;
  }

  private User updateExistingUser(User existingUser, OAuth2User oauth2User) {
    log.info("Existing OAuth2 user logged in with email: {}", existingUser.getEmail());
    return existingUser;
  }
}
