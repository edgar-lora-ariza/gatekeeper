package com.white.label.gatekeeper.core.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

public class PemUtils {

  private PemUtils() {}

  private static final String PEM_LINE_BREAK_REGEX = "(.{64})";

  public static String enforcePemFormat(String pemContent) {
    if (!pemContent.contains(System.lineSeparator())
        && (pemContent.contains("CERTIFICATE") || pemContent.contains("KEY"))) {
      return pemContent.replace("-----END", System.lineSeparator() + "-----END")
          .replace("CERTIFICATE-----", "CERTIFICATE-----" + System.lineSeparator())
          .replace("KEY-----", "KEY-----" + System.lineSeparator())
          .replaceAll(PEM_LINE_BREAK_REGEX, "$1" + System.lineSeparator());
    }
    return pemContent;
  }

  public static String enforcePemCertFormat(String pemCertContent) {
    if (!pemCertContent.contains(System.lineSeparator()) && !pemCertContent.contains("CERTIFICATE")) {
      return "-----BEGIN CERTIFICATE-----" + System.lineSeparator()
          + pemCertContent.replaceAll(PEM_LINE_BREAK_REGEX, "$1" + System.lineSeparator())
          + System.lineSeparator() + "-----END CERTIFICATE-----";
    }
    return pemCertContent;
  }

  public static String enforcePemPrivateKeyFormat(String pemPrivateKeyContent) {
    if (!pemPrivateKeyContent.contains(System.lineSeparator()) && !pemPrivateKeyContent.contains("PRIVATE KEY")) {
      return "-----BEGIN PRIVATE KEY-----" + System.lineSeparator()
          + pemPrivateKeyContent.replaceAll(PEM_LINE_BREAK_REGEX, "$1" + System.lineSeparator())
          + System.lineSeparator() + "-----END PRIVATE KEY-----";
    }
    return pemPrivateKeyContent;
  }

  public static Certificate getCertificate(String certificate)
      throws IOException, CertificateException {
    String pemCertEnforcedContent = enforcePemCertFormat(certificate);
    String pemEnforcedContent = enforcePemFormat(pemCertEnforcedContent);
    StringReader reader = new StringReader(pemEnforcedContent);
    PEMParser pemParser = new PEMParser(reader);
    X509CertificateHolder certificateHolder = (X509CertificateHolder) pemParser.readObject();
    CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
    return certFactory.generateCertificate(new ByteArrayInputStream(certificateHolder.getEncoded()));
  }

  public static PublicKey getPublicKey(Certificate certificate) {
    return certificate.getPublicKey();
  }

  public static PrivateKey getPrivateKey(String privateKey) throws IOException {
    String pemPrivateKeyEnforcedContent = PemUtils.enforcePemPrivateKeyFormat(privateKey);
    String pemEnforcedContent = enforcePemFormat(pemPrivateKeyEnforcedContent);
    StringReader reader = new StringReader(pemEnforcedContent);
    PEMParser pemParser = new PEMParser(reader);
    JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
    Object object = pemParser.readObject();
    if (object instanceof PrivateKeyInfo privateKeyInfo) {
      return converter.getPrivateKey(privateKeyInfo);
    }
    if (object instanceof PEMKeyPair pemKeyPair) {
      return converter.getPrivateKey((pemKeyPair).getPrivateKeyInfo());
    }
    throw new IOException("Unsupported key type: " + object.getClass().getName());
  }
}
