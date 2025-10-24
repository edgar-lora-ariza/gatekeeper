package com.bedrock.gatekeeper.keys.dtos;

import com.bedrock.gatekeeper.keys.model.SigningKey;
import java.util.List;

public record DeleteAllSigningKeysRequest(List<SigningKey> signingKeys) {
}
