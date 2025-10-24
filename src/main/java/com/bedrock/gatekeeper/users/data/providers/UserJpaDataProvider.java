package com.bedrock.gatekeeper.users.data.providers;

import com.bedrock.gatekeeper.users.entities.UserEntity;
import com.bedrock.gatekeeper.users.model.CustomUser;
import com.bedrock.gatekeeper.users.ports.UserDataProvider;
import com.bedrock.gatekeeper.users.repositories.UserRepository;
import io.micrometer.observation.annotation.Observed;
import java.util.Objects;
import java.util.Optional;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
@Observed
public class UserJpaDataProvider implements UserDataProvider {

  private final UserRepository userRepository;
  private final ConversionService conversionService;

  public UserJpaDataProvider(UserRepository userRepository,
                             ConversionService conversionService) {
    this.userRepository = userRepository;
    this.conversionService = conversionService;
  }

  @Override
  public CustomUser save(User user) {
    UserEntity entity = conversionService.convert(user, UserEntity.class);
    userRepository.findByUsername(user.getUsername())
        .ifPresent(userEntity -> Objects.requireNonNull(entity)
            .setId(userEntity.getId()));
    UserEntity savedEntity = userRepository.save(Objects.requireNonNull(entity));
    return conversionService.convert(savedEntity, CustomUser.class);
  }

  @Override
  public Optional<CustomUser> findByUsername(String username) {
    Optional<UserEntity> userOptional = userRepository
        .findByUsername(username);

    if (userOptional.isEmpty()) {
      return Optional.empty();
    }

    UserEntity userEntity = userOptional.get();
    CustomUser user = conversionService.convert(userEntity, CustomUser.class);
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
