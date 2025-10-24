package com.bedrock.gatekeeper.commons.converters;

import com.bedrock.gatekeeper.users.model.CustomUser;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * A custom Jackson module to override the default deserialization behavior for specific classes.
 * This is crucial for custom UserDetails implementations that extend Spring's User class.
 */
public class CustomJacksonModule extends SimpleModule {

    @Override
    public void setupModule(SetupContext context) {
        // By applying this mix-in, we tell Jackson to NOT use the default UserDeserializer
        // for our CustomUser class, allowing it to be deserialized into its proper type.
        context.setMixInAnnotations(CustomUser.class, CustomUserMixin.class);
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    @JsonDeserialize(using = JsonDeserializer.None.class)
    private static class CustomUserMixin {
        @JsonCreator
        public CustomUserMixin() {
        }
    }
}