package com.example;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CodeConfig {
    private static volatile CodeConfig instance;
    private final int keyLength;
    private final int iterationCount;
    private final char[] secretKey;
    private final byte[] salt;
    private static volatile boolean initialized = false;
    
    CodeConfig(int keyLength, int iterationCount, String secretKey, String salt) {
        this.keyLength = keyLength;
        this.iterationCount = iterationCount;
        this.secretKey = secretKey.toCharArray();
        this.salt = salt.getBytes();
    }

    public static synchronized void initialize(String configFile) {
        if (initialized) {
            throw new IllegalStateException("Config already initialized. Cannot reinitialize.");
        }
        
        try {
            // Read configuration from secure source
            GenerateCode generator = new GenerateCode(configFile);
            generator.buildInputFile();
            ArrayList<String> list = generator.readInputFile();
            int keyLen = Integer.parseInt(list.get(0));
            int iterCount = Integer.parseInt(list.get(1));
            String secret = list.get(2);
            String saltStr = list.get(3);
            instance = new CodeConfig(keyLen, iterCount, secret, saltStr);
            initialized = true;
            // Clear sensitive data from memory
            list.clear();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Config", e);
        }
    }
    
    public static CodeConfig getInstance() {
        if (!initialized || instance == null) {
            throw new IllegalStateException("Config not initialized. Call initialize() first.");
        }
        return instance;
    }

    int getKeyLength() {
        return keyLength;
    }
    
    int getIterationCount() {
        return iterationCount;
    }
    
    // Returns a COPY of the char array, not the original
    char[] getSecretKeyChars() {
        return secretKey.clone();
    }
    
    // Returns a COPY of the salt, not the original
    byte[] getSalt() {
        return salt.clone();
    }
    
    public static boolean isInitialized() {
        return initialized;
    }
    
    public void destroy() {
        // Overwrite sensitive data
        if (secretKey != null) {
            java.util.Arrays.fill(secretKey, '\0');
        }
        if (salt != null) {
            java.util.Arrays.fill(salt, (byte) 0);
        }
    }
    // tools
    private void Service() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    public static String encrypt(String dataToEncrypt) {
        if (!CodeConfig.isInitialized()) {
            throw new IllegalStateException("CryptoConfig not initialized");
        }
        
        try {
            CodeConfig config = CodeConfig.getInstance();
            // Generate IV
            SecureRandom secureRandom = new SecureRandom();
            byte[] iv = new byte[16];
            secureRandom.nextBytes(iv);
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            // Generate key from password
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(
                config.getSecretKeyChars(), 
                config.getSalt(), 
                config.getIterationCount(), 
                config.getKeyLength()
            );
            SecretKey temp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(temp.getEncoded(), "AES");
            // Encrypt
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            byte[] cipherText = cipher.doFinal(dataToEncrypt.getBytes("UTF-8"));
            // Combine IV and ciphertext
            byte[] encryptedData = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, encryptedData, 0, iv.length);
            System.arraycopy(cipherText, 0, encryptedData, iv.length, cipherText.length);
            return Base64.getEncoder().encodeToString(encryptedData);
            
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public static String decrypt(String strToDecrypt) {
        if (!CodeConfig.isInitialized()) {
            throw new IllegalStateException("CryptoConfig not initialized");
        }
        
        try {
            CodeConfig config = CodeConfig.getInstance();
            byte[] encryptedData = Base64.getDecoder().decode(strToDecrypt);
            // Extract IV
            byte[] iv = new byte[16];
            System.arraycopy(encryptedData, 0, iv, 0, iv.length);
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            // Generate key from password
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(
                config.getSecretKeyChars(),
                config.getSalt(),
                config.getIterationCount(),
                config.getKeyLength()
            );
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), "AES");
            // Decrypt
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec);
            byte[] cipherText = new byte[encryptedData.length - 16];
            System.arraycopy(encryptedData, 16, cipherText, 0, cipherText.length);
            byte[] decryptedText = cipher.doFinal(cipherText);
            return new String(decryptedText, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}