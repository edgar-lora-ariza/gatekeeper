package com.white.label.gatekeeper.core.model;

public record SigningKey(String id, String keyIdentifier, String certificate, String privateKey) {
}
