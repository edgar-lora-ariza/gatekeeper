package com.bedrock.gatekeeper.users.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ExternalUser {

  private String id;
  private String email;
}
