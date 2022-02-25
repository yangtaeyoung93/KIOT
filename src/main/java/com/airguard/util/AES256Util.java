package com.airguard.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;



@Component
public class AES256Util {

  private static byte[] key = Sha256EncryptUtil.shaEncoderByte(CommonConstant.AES256_KEY);
  private static byte[] iv = new byte[16];

  public AES256Util() throws UnsupportedEncodingException {

    System.arraycopy(key, 0, iv, 0, 16);

  }

  public static String encrypt(String str)
          throws GeneralSecurityException, UnsupportedEncodingException {
    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
    SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
    IvParameterSpec ivParamSpec = new IvParameterSpec(iv);

    c.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);
    byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
    return new String(Base64.encodeBase64(encrypted));
  }

  public static String decrypt(String str)
          throws GeneralSecurityException, UnsupportedEncodingException {
    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
    SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
    IvParameterSpec ivParamSpec = new IvParameterSpec(iv);
    c.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

    byte[] decodedBytes = Base64.decodeBase64(str);
    byte[] decrypted = c.doFinal(decodedBytes);
    return new String(decrypted, StandardCharsets.UTF_8);
  }
}
