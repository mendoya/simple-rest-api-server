package com.kakaopay.payment.advice.exceptions;

public class PaymentResourceNotFoundException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6658306809929628967L;

	public PaymentResourceNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    public PaymentResourceNotFoundException(String msg) {
        super(msg);
    }

    public PaymentResourceNotFoundException() {
        super();
    }
}
