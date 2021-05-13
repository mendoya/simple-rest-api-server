package com.kakaopay.payment.entity;

import java.util.StringJoiner;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.kakaopay.payment.advice.exceptions.EncryptException;
import com.kakaopay.payment.util.AESUtil;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Card {
	private final String separator = "|";
	
	@Column(name = "CARD_NO", nullable = false, length = 16)
    private String cardNo;

    @Column(name = "EXPIRE", nullable = false)
    private String expire;
    
    @Column(name = "CVC", nullable = false, length = 3)
    private String cvc;
    
    public Card(String encryptedCardString) {
    	String decryptString = AESUtil.decrypt(encryptedCardString);
    	String[] cardStrings = decryptString.split("\\" + separator);
    	this.cardNo = cardStrings[0];
    	this.expire = cardStrings[1];
    	this.cvc = cardStrings[2];
	}
    
    @Builder
    public Card(String cardNo, String expire, String cvc) {
    	this.cardNo = cardNo;
    	this.expire = expire;
    	this.cvc = cvc;
	}
    
    public String getEncryptString() throws EncryptException{
    	StringJoiner joiner = new StringJoiner(separator);
    	
    	joiner.add(this.cardNo);
    	joiner.add(this.expire);
    	joiner.add(this.cvc);
    	
		return AESUtil.encrypt(joiner.toString());
    	
    }
}
