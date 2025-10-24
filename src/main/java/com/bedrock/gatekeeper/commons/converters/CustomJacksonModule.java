package com.bedrock.gatekeeper.commons.converters;

import com.bedrock.gatekeeper.users.model.CustomUser;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class CustomJacksonModule extends SimpleModule {

    @Override
    public void setupModule(SetupContext context) {
        context.setMixInAnnotations(CustomUser.class, CustomUserMixin.class);
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    @JsonDeserialize
    private static class CustomUserMixin {
        @JsonCreator
        public CustomUserMixin() {
          //No Implementation
        }
    }
}