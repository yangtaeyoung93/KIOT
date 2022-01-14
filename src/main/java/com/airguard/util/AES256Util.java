package com.airguard.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

@Component
public class AES256Util {

  private static String iv;
  private static Key keySpec;

  public AES256Util() throws UnsupportedEncodingException {
    iv = CommonConstant.AES256_KEY.substring(0, 16);
    byte[] keyBytes = new byte[16];
    byte[] b = CommonConstant.AES256_KEY.getBytes(StandardCharsets.UTF_8);
    int len = b.length;
    if (len > keyBytes.length) {
      len = keyBytes.length;
    }
    System.arraycopy(b, 0, keyBytes, 0, len);

    AES256Util.keySpec = new SecretKeySpec(keyBytes, "AES");
  }

  public static String encrypt(String str)
      throws GeneralSecurityException, UnsupportedEncodingException {
    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
    c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
    byte[] encrypted = c.doFinal(str.getBytes(StandardCharsets.UTF_8));
    return new String(Base64.encodeBase64(encrypted));
  }

  public static String decrypt(String str)
      throws GeneralSecurityException, UnsupportedEncodingException {
    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
    c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
    byte[] byteStr = Base64.decodeBase64(str.getBytes());
    return new String(c.doFinal(byteStr), StandardCharsets.UTF_8);
  }
}
