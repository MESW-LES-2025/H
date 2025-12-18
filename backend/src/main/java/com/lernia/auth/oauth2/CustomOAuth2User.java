package com.lernia.auth.oauth2;

import com.lernia.auth.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

  private final OAuth2User oauth2User;
  private final UserEntity user;

  public CustomOAuth2User(OAuth2User oauth2User, UserEntity user) {
    this.oauth2User = oauth2User;
    this.user = user;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return oauth2User.getAttributes();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name()));
  }

  @Override
  public String getName() {
    return user.getUsername();
  }

  public UserEntity getUser() {
    return user;
  }
}
