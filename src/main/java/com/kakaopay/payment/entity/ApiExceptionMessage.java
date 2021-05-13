package com.kakaopay.payment.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiExceptionMessage {
	private int code;
	private String message;
	@Builder.Default
	private String detailMessage = "";
}
