package com.white.label.gatekeeper.core.services;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public interface EncryptionService {

  String encrypt(String data, String key)
      throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
      BadPaddingException, InvalidAlgorithmParameterException;
  String decrypt(String encryptedData, String key)
      throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
      BadPaddingException, InvalidAlgorithmParameterException;
}
