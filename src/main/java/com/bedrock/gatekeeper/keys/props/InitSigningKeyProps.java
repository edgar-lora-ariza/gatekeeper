package com.bedrock.gatekeeper.keys.props;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gatekeeper.admin-console.init.signing-key")
public record InitSigningKeyProps(String keyIdentifier, String certificate, String privateKey) {
}
