package com.kakaopay.payment.advice.exceptions;

import lombok.Getter;

@Getter
public class PaymentCancelException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6658306809929628967L;

	public PaymentCancelException(String msg, Throwable t) {
        super(msg, t);
    }

    public PaymentCancelException(String msg) {
        super(msg);
    }

    public PaymentCancelException() {
        super();
    }
}
