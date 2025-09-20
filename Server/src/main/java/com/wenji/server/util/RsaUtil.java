package com.wenji.server.util;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class RsaUtil {
    
    // RSA算法
    private static final String RSA_ALGORITHM = "RSA";
    
    // 密钥大小
    private static final int KEY_SIZE = 2048;
    
    // 从Base64编码的字符串中获取公钥
    public PublicKey getPublicKey(String publicKeyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }
    
    // 从Base64编码的字符串中获取私钥
    public PrivateKey getPrivateKey(String privateKeyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }
    
    // 使用公钥加密数据
    public String encrypt(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
    
    // 使用私钥解密数据
    public String decrypt(String encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    
    // 验证公钥格式是否正确
    public boolean isValidPublicKey(String publicKeyStr) {
        try {
            getPublicKey(publicKeyStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // 验证私钥格式是否正确
    public boolean isValidPrivateKey(String privateKeyStr) {
        try {
            getPrivateKey(privateKeyStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // 生成RSA密钥对
    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        SecureRandom secureRandom = new SecureRandom();
        keyPairGenerator.initialize(KEY_SIZE, secureRandom);
        return keyPairGenerator.generateKeyPair();
    }
    
    // 生成并返回Base64编码的密钥对
    public Map<String, String> generateEncodedKeyPair() throws NoSuchAlgorithmException {
        KeyPair keyPair = generateKeyPair();
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        
        Map<String, String> keyPairMap = new HashMap<>();
        keyPairMap.put("publicKey", publicKey);
        keyPairMap.put("privateKey", privateKey);
        
        return keyPairMap;
    }
}