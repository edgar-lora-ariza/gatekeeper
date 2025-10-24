package com.white.label.gatekeeper.infrastructure.data.providers;

import com.white.label.gatekeeper.core.ports.UserPort;
import com.white.label.gatekeeper.infrastructure.data.providers.entities.UserEntity;
import com.white.label.gatekeeper.infrastructure.data.providers.repositories.UserRepository;
import java.util.Objects;
import java.util.Optional;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class UserJpaDataProvider implements UserPort {

  private final UserRepository userRepository;
  private final ConversionService conversionService;

  public UserJpaDataProvider(UserRepository userRepository,
                             ConversionService conversionService) {
    this.userRepository = userRepository;
    this.conversionService = conversionService;
  }

  @Override
  public User save(User user) {
    UserEntity entity = conversionService.convert(user, UserEntity.class);

    UserEntity savedEntity = userRepository.save(Objects.requireNonNull(entity));
    return conversionService.convert(savedEntity, User.class);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    Optional<UserEntity> userOptional = userRepository
        .findByUsername(username);

    if (userOptional.isEmpty()) {
      return Optional.empty();
    }

    UserEntity userEntity = userOptional.get();
    User user = conversionService.convert(userEntity, User.class);
    return Optional.of(Objects.requireNonNull(user));
  }

  @Override
  public void deleteByUsername(String username) {
    userRepository.findByUsername(username).ifPresent(userRepository::delete);
  }

  @Override
  public boolean userExists(String username) {
    return userRepository.findByUsername(username).isPresent();
  }
}
