package com.kakaopay.payment.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {
	private String paymentId;
	private String cardNo;
	private String expire;
	private String cvc;
	private String paymentDivision;
	private long amount;
	private long vat;
}
