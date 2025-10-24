package com.bedrock.gatekeeper.keys.model;

public record SigningKey(String id, String keyIdentifier, String certificate, String privateKey) {
}
