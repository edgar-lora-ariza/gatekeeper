package com.bedrock.gatekeeper.users.converters;

import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsToUserConverter implements Converter<UserDetails, User> {
  @Override
  public User convert(UserDetails source) {
    return new User(
        source.getUsername(),
        source.getPassword(),
        source.isEnabled(),
        source.isAccountNonExpired(),
        source.isCredentialsNonExpired(),
        source.isAccountNonLocked(),
        source.getAuthorities().stream()
            .map(authorityEntity -> new SimpleGrantedAuthority(authorityEntity.getAuthority()))
            .collect(Collectors.toSet()));
  }
}
