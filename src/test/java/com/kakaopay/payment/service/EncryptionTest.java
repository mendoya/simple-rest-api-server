package com.kakaopay.payment.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.kakaopay.payment.util.AESUtil;

public class EncryptionTest {
	@Test
	public void testEncryption() throws Exception {
		String text = "0000111122223333|0422|040";
		String encrypt = AESUtil.encrypt(text);
		System.out.println(encrypt);
		assertTrue(encrypt.length() <= 300);
		assertTrue(text.equals(AESUtil.decrypt(encrypt)));
		String decrypt = AESUtil.decrypt(encrypt);
		for(String s : decrypt.split("\\|")) {
			System.out.println(s);
		}
		
		
	}
	
}
