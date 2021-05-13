package com.kakaopay.payment.util;

import java.nio.ByteBuffer;
import java.util.UUID;

public class IDUtil {    
    public static String uniqueId(int length) {
    	StringBuffer buffer = new StringBuffer();
    	while(buffer.length() < length) {
	    	UUID uuid = UUID.randomUUID(); 
	    	long l = ByteBuffer.wrap(uuid.toString().replaceAll("-", "").getBytes()).getLong();
	    	
	    	buffer.append(Long.toString(l, Character.MAX_RADIX));
    	}
    	
    	return buffer.substring(0, length);
    }
   
}
