package com.kakaopay.payment.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentUpdateResponse {
    private String paymentId;
    private String paymentMessage;
}
