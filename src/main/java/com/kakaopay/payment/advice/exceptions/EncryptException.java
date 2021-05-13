package com.kakaopay.payment.advice.exceptions;

public class EncryptException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6658306809929628967L;

	public EncryptException(String msg, Throwable t) {
        super(msg, t);
    }

    public EncryptException(String msg) {
        super(msg);
    }

    public EncryptException() {
        super();
    }
}
