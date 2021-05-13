package com.kakaopay.payment.util;

public class PaymentStringUtils {
	public static String getCardMasking(String src) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(src.substring(0, 6));
		buffer.append("*".repeat(src.length() - 9));
		buffer.append(src.substring(src.length() - 3));
		
		return buffer.toString();
	}
}
