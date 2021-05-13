package com.kakaopay.payment.entity;


import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.common.base.Strings;
import com.kakaopay.payment.util.IDUtil;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PAYMENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {
	@Id
	@Column(name = "ID", nullable = false, length = 20, unique = true)
	private String paymentId;
	
	@Embedded
	private Card card;
	
    @Column(name = "INSTALLMENT", nullable = false)
    private int installment ;

    @Column(name = "AMOUNT", nullable = false)
    private long amount;
    
    @Column(name = "VAT", nullable = false)
    private long vat;
    
    @Column(name = "ORG_ID", nullable = true, length = 20)
	private String orgPaymentId;
    
    @Column(name = "MESSAGE", nullable = true, length = 450)
	private String message;
    
    public String getPaymentDivisionString() {
    	return this.getOrgPaymentId() == null ?  "PAYMENT" : "CANCEL";
    }
    
    private String toMessage() {
    	StringBuffer buffer = new StringBuffer();
    	
    	buffer.append(Strings.padEnd(this.getPaymentDivisionString(), 10, ' '));
    	buffer.append(Strings.padEnd(this.paymentId, 20, ' '));
    	
    	buffer.append(Strings.padEnd(this.card.getCardNo(), 20, ' '));
    	buffer.append(Strings.padStart(String.valueOf(this.installment), 2, '0'));
    	buffer.append(Strings.padStart(this.card.getExpire(), 4, '0'));
    	buffer.append(Strings.padStart(this.card.getCvc(), 3, '0'));
    	buffer.append(Strings.padStart(String.valueOf(this.amount), 10, ' '));
    	buffer.append(Strings.padStart(String.valueOf(this.vat), 10, '0'));
    	buffer.append(Strings.padEnd(this.orgPaymentId == null ? "" : this.orgPaymentId, 20, ' '));
    	buffer.append(Strings.padEnd(this.getCard().getEncryptString(), 300, ' '));
		buffer.append(" ".repeat(47));
		
		buffer.insert(0, Strings.padStart(String.valueOf(buffer.length()), 4, ' '));
    	
    	return buffer.toString();
    }
    
    @Builder
    public Payment(Card card, int installment, long amount, long vat, String orgPaymentId) {
        this.paymentId = IDUtil.uniqueId(20);
        this.installment = installment;
        this.card = card;
		this.amount = amount;
		this.vat = vat;
		this.orgPaymentId = orgPaymentId;
		this.message = toMessage();
		
    }
}
