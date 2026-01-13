package com.example.foods.service;

import com.example.foods.entity.User;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@RequiredArgsConstructor
@Getter
public class CustomOAuth2User implements OAuth2User, Serializable {

  private static final long serialVersionUID = 1L;

  private final User user;
  private final Map<String, Object> attributes;

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return user.getAuthorities();
  }

  @Override
  public String getName() {
    return user.getUsername();
  }

  public String getEmail() {
    return user.getEmail();
  }

  public Long getId() {
    return user.getId();
  }

  public String getRole() {
    return user.getRole();
  }
}
