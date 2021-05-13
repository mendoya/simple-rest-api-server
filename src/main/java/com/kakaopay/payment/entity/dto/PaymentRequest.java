package com.kakaopay.payment.entity.dto;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {
	@NotEmpty
	@Pattern(regexp="[0-9]{10,16}")
	private String cardNo;
	@DecimalMin(value="0") @DecimalMax(value="12") 
    private int installment;
	@NotEmpty
	@Pattern(regexp="[0-9][1-9][0-9][0-9]")
    private String expire;
	@NotEmpty
	@Pattern(regexp="[0-9]{3}")
    private String cvc;
	@DecimalMin(value="100")
	@DecimalMax(value="9999999999")
    private long amount;
	@DecimalMin(value="11")
	@DecimalMax(value="9999999999")
    private Long vat;
}
