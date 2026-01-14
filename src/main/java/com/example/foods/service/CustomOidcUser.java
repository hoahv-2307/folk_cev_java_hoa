package com.example.foods.service;

import com.example.foods.entity.User;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class CustomOidcUser implements OidcUser, Serializable {

  private static final long serialVersionUID = 1L;

  private final User user;
  private final Map<String, Object> attributes;

  private transient OidcIdToken idToken;

  public CustomOidcUser(User user, Map<String, Object> attributes, OidcIdToken idToken) {
    this.user = user;
    this.attributes = new HashMap<>(attributes);
    this.idToken = idToken;
  }

  @Override
  public Map<String, Object> getClaims() {
    return idToken != null ? idToken.getClaims() : attributes;
  }

  @Override
  public OidcUserInfo getUserInfo() {
    return null;
  }

  @Override
  public OidcIdToken getIdToken() {
    return idToken;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return java.util.List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
  }

  @Override
  public String getName() {
    return user.getUsername();
  }

  public User getUser() {
    return user;
  }
}
