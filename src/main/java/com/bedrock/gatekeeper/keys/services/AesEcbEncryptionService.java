package com.bedrock.gatekeeper.keys.services;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AesEcbEncryptionService implements EncryptionService {

  public static final String AES_ALGORITHM = "AES";
  private static final String AES_GCM_NO_PADDING_TRANSFORMATION = "AES/GCM/NoPadding";
  private static final int GCM_IV_LENGTH = 12;
  private static final int GCM_TAG_LENGTH = 128;
  public static final String SHA_256_ALGORITHM = "SHA-256";

  @Override
  public String encrypt(String data, String key)
      throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
      BadPaddingException, InvalidAlgorithmParameterException {
    SecretKey secretKey = prepareSecretKey(key);
    Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING_TRANSFORMATION);
    byte[] iv = generateIv();
    GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
    byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
    byte[] combined = new byte[iv.length + encryptedBytes.length];
    System.arraycopy(iv, 0, combined, 0, iv.length);
    System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);
    return Base64.getEncoder().encodeToString(combined);
  }

  @Override
  public String decrypt(String encryptedData, String key)
      throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
      BadPaddingException, InvalidAlgorithmParameterException {
    byte[] decodedData = Base64.getDecoder().decode(encryptedData);
    byte[] iv = Arrays.copyOfRange(decodedData, 0, GCM_IV_LENGTH);
    byte[] encryptedBytes = Arrays.copyOfRange(decodedData, GCM_IV_LENGTH, decodedData.length);

    SecretKey secretKey = prepareSecretKey(key);
    Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING_TRANSFORMATION);
    GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
    cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
    byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
    return new String(decryptedBytes, StandardCharsets.UTF_8);
  }

  private SecretKey prepareSecretKey(String key) throws NoSuchAlgorithmException {
    if (Objects.isNull(key) || key.isBlank()) {
      log.error("Encryption key not set");
      throw new IllegalStateException("Encryption key not set");
    }

    MessageDigest sha = MessageDigest.getInstance(SHA_256_ALGORITHM);
    byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
    byte[] hash = sha.digest(keyBytes);

    byte[] truncatedHash = Arrays.copyOf(hash, 16);
    return new SecretKeySpec(truncatedHash, AES_ALGORITHM);
  }

  private byte[] generateIv() {
    byte[] iv = new byte[GCM_IV_LENGTH];
    new SecureRandom().nextBytes(iv);
    return iv;
  }
}
