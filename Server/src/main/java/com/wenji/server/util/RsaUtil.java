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
        // 如果是PEM格式，提取Base64部分
        String cleanedKey = publicKeyStr;
        if (publicKeyStr.contains("-----BEGIN PUBLIC KEY-----")) {
            cleanedKey = publicKeyStr
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", ""); // 移除所有空白字符
        }
        
        byte[] keyBytes = Base64.getDecoder().decode(cleanedKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }
    
    // 从Base64编码的字符串中获取私钥
    public PrivateKey getPrivateKey(String privateKeyStr) throws Exception {
        // 如果是PEM格式，提取Base64部分
        String cleanedKey = privateKeyStr;
        if (privateKeyStr.contains("-----BEGIN PRIVATE KEY-----")) {
            cleanedKey = privateKeyStr
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", ""); // 移除所有空白字符
        }
        
        byte[] keyBytes = Base64.getDecoder().decode(cleanedKey);
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
        
        // 检查是否为有效的Base64字符串
        System.out.println("检查Base64格式: " + encryptedData);
        if (!isValidBase64(encryptedData)) {
            System.out.println("不是有效的Base64格式，可能是明文密码: " + encryptedData);
            // 如果不是有效的Base64格式，直接返回原始字符串（可能是明文密码）
            return encryptedData;
        }
        System.out.println("Base64格式验证通过: " + encryptedData);
        
        // 智能选择Base64解码器
        byte[] encryptedBytes;
        try {
            // 检查是否包含URL安全Base64的特殊字符（- 或 _）
            if (encryptedData.contains("-") || encryptedData.contains("_")) {
                System.out.println("检测到URL安全Base64字符，使用URL安全解码器: " + encryptedData);
                encryptedBytes = Base64.getUrlDecoder().decode(encryptedData);
                System.out.println("URL安全Base64解码成功");
            } else {
                System.out.println("使用标准Base64解码器: " + encryptedData);
                encryptedBytes = Base64.getDecoder().decode(encryptedData);
                System.out.println("标准Base64解码成功");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Base64解码失败: " + e.getMessage());
            // 如果解码失败，尝试另一种解码器
            try {
                if (encryptedData.contains("-") || encryptedData.contains("_")) {
                    System.out.println("URL安全解码失败，尝试标准Base64解码: " + encryptedData);
                    encryptedBytes = Base64.getDecoder().decode(encryptedData);
                    System.out.println("标准Base64解码成功");
                } else {
                    System.out.println("标准解码失败，尝试URL安全Base64解码: " + encryptedData);
                    encryptedBytes = Base64.getUrlDecoder().decode(encryptedData);
                    System.out.println("URL安全Base64解码成功");
                }
            } catch (IllegalArgumentException e2) {
                System.out.println("所有Base64解码方式都失败: " + e2.getMessage());
                throw new IllegalArgumentException("Base64解码失败: " + e2.getMessage());
            }
        }
        
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    
    // 检查字符串是否为有效的Base64格式
    private boolean isValidBase64(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        
        // 对于明文密码（如"12345678"），直接返回false，让它走明文处理逻辑
        if (str.matches("^[a-zA-Z0-9]+$") && str.length() < 20) {
            return false;
        }
        
        // Base64字符集：A-Z, a-z, 0-9, +, /, = (标准Base64)
        // 以及 - 和 _ (URL安全Base64)
        String base64Pattern = "^[A-Za-z0-9+/\\-_]*={0,2}$";
        return str.matches(base64Pattern);
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
        String publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        
        // 格式化公钥为PEM格式
        String publicKeyPem = formatPublicKeyToPem(publicKeyBase64);
        String privateKeyPem = formatPrivateKeyToPem(privateKeyBase64);
        
        Map<String, String> keyPairMap = new HashMap<>();
        keyPairMap.put("publicKey", publicKeyPem);
        keyPairMap.put("privateKey", privateKeyPem);
        
        return keyPairMap;
    }
    
    // 将Base64公钥格式化为PEM格式
    private String formatPublicKeyToPem(String publicKeyBase64) {
        StringBuilder sb = new StringBuilder();
        sb.append("-----BEGIN PUBLIC KEY-----\n");
        
        // 每64个字符换行
        int index = 0;
        while (index < publicKeyBase64.length()) {
            int endIndex = Math.min(index + 64, publicKeyBase64.length());
            sb.append(publicKeyBase64.substring(index, endIndex)).append("\n");
            index = endIndex;
        }
        
        sb.append("-----END PUBLIC KEY-----");
        return sb.toString();
    }
    
    // 将Base64私钥格式化为PEM格式
    private String formatPrivateKeyToPem(String privateKeyBase64) {
        StringBuilder sb = new StringBuilder();
        sb.append("-----BEGIN PRIVATE KEY-----\n");
        
        // 每64个字符换行
        int index = 0;
        while (index < privateKeyBase64.length()) {
            int endIndex = Math.min(index + 64, privateKeyBase64.length());
            sb.append(privateKeyBase64.substring(index, endIndex)).append("\n");
            index = endIndex;
        }
        
        sb.append("-----END PRIVATE KEY-----");
        return sb.toString();
    }
}