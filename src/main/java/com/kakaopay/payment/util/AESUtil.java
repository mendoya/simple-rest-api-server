package com.kakaopay.payment.util;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.kakaopay.payment.advice.exceptions.EncryptException;

public class AESUtil{

    private final static String key = "12345678901234567890123456789012";

    public static String encrypt(String plainText) {
        int ivSize = 16;
        byte[] iv = new byte[ivSize];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        AlgorithmParameterSpec ivspec = new IvParameterSpec(iv);
        try {
        	SecretKey keyspec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
	        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
	        cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
	
	        int blockSize = 128; //block size
	        byte[] dataBytes = plainText.getBytes("UTF-8");
	
	        int plaintextLength = dataBytes.length;
	        int fillChar = ((blockSize - (plaintextLength % blockSize)));
	        plaintextLength += fillChar; //pad 
	
	        byte[] plaintext = new byte[plaintextLength];
	        Arrays.fill(plaintext, (byte) fillChar);
	        System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
	
	        byte[] cipherBytes = cipher.doFinal(plaintext);
	
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
	        outputStream.write( iv );
	        outputStream.write( cipherBytes );
	
	        byte [] encryptedIvText = outputStream.toByteArray();
	        return new String(Base64.getEncoder().encode(encryptedIvText), "UTF-8");
        } catch(Exception ex){
	    	throw new EncryptException();
	    }
    }

    public static String decrypt(String encryptedIvText) {
        byte [] encryptedIvTextBytes = Base64.getDecoder().decode(encryptedIvText);

        int ivSize = 16;
        byte[] iv = new byte[ivSize];
        System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length);

        int encryptedSize = encryptedIvTextBytes.length - ivSize;
        byte[] encryptedBytes = new byte[encryptedSize];
        System.arraycopy(encryptedIvTextBytes, ivSize, encryptedBytes, 0, encryptedSize);
        try {
	        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
	        SecretKey keyspec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
	        AlgorithmParameterSpec ivspec = new IvParameterSpec(iv);
	        cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
	        byte[] aesdecode = cipher.doFinal(encryptedBytes);
	        byte[] origin = new byte[aesdecode.length - (aesdecode[aesdecode.length - 1])];
	        System.arraycopy(aesdecode, 0, origin, 0, origin.length);
	        
	        return new String(origin, "UTF-8");
        } catch(Exception ex){
	    	throw new EncryptException();
	    }
    }
}
