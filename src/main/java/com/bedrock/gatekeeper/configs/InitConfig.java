package com.bedrock.gatekeeper.configs;

import com.bedrock.gatekeeper.users.usecases.InitUserUserCase;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitConfig {

  @Bean
  public ApplicationRunner initUsers(InitUserUserCase initUser) {
    return  _ -> initUser.init();
  }
}
