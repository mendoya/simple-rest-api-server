package com.kakaopay.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakaopay.payment.advice.exceptions.PaymentCancelException;
import com.kakaopay.payment.advice.exceptions.PaymentResourceNotFoundException;
import com.kakaopay.payment.entity.Card;
import com.kakaopay.payment.entity.Payment;
import com.kakaopay.payment.entity.dto.PaymentCancelRequest;
import com.kakaopay.payment.entity.dto.PaymentRequest;
import com.kakaopay.payment.entity.dto.PaymentResponse;
import com.kakaopay.payment.entity.dto.PaymentUpdateResponse;
import com.kakaopay.payment.mock.CardSystemPaymentRepository;
import com.kakaopay.payment.util.PaymentStringUtils;

import lombok.AllArgsConstructor;

@Transactional
@AllArgsConstructor
@Service
public class PaymentService {
	private CardSystemPaymentRepository repository;
	
	public PaymentResponse findPaymentById(String paymentId) {
		Payment payment = repository.findById(paymentId).orElseThrow(
				() -> new PaymentResourceNotFoundException(paymentId));
		
		return PaymentResponse.builder()
				.paymentId(payment.getPaymentId())
				.cardNo(PaymentStringUtils.getCardMasking(payment.getCard().getCardNo()))
				.expire(payment.getCard().getExpire())
				.cvc(payment.getCard().getCvc())
				.paymentDivision(payment.getPaymentDivisionString())
				.amount(payment.getAmount())
				.vat(payment.getVat())
				.build();
	}

	public PaymentResponse findPaymentCancelById(String paymentId) {
		Payment payment = repository.findById(paymentId).orElseThrow(
				() -> new PaymentResourceNotFoundException(paymentId));
		
		return PaymentResponse.builder()
				.paymentId(payment.getPaymentId())
				.cardNo(PaymentStringUtils.getCardMasking(payment.getCard().getCardNo()))
				.expire(payment.getCard().getExpire())
				.cvc(payment.getCard().getCvc())
				.paymentDivision(payment.getPaymentDivisionString())
				.amount(payment.getAmount())
				.vat(payment.getVat())
				.build();
	}

	public PaymentUpdateResponse createPayment(PaymentRequest paymentRequest) {
		Card card = Card.builder()
				.cardNo(paymentRequest.getCardNo())
				.cvc(paymentRequest.getCvc())
				.expire(paymentRequest.getExpire())
				.build();
		Payment payment = Payment.builder()
				.card(card)
				.installment(paymentRequest.getInstallment())
				.amount(paymentRequest.getAmount())
				.vat(paymentRequest.getVat() == null ? (long)(paymentRequest.getAmount() / 11) : paymentRequest.getVat().longValue())
				.build();
		repository.save(payment);
		
		return PaymentUpdateResponse.builder()
				.paymentId(payment.getPaymentId())
				.paymentMessage(payment.getMessage())
				.build();
	}

	public PaymentUpdateResponse createPaymentCancel(PaymentCancelRequest paymentCancelRequest) {
		List<Payment> cancelList = repository.findByOrgPaymentId(paymentCancelRequest.getOrgPaymentId());
		
		long canceledAmount = 0;
		long canceledVat = 0;
		for(Payment cancelPayment : cancelList) {
			canceledAmount += cancelPayment.getAmount();
			canceledVat += cancelPayment.getVat();
		}
		
		Payment payment = repository.findById(paymentCancelRequest.getOrgPaymentId())
				.orElseThrow(PaymentResourceNotFoundException::new);
		
		if (payment.getAmount() < paymentCancelRequest.getAmount() + canceledAmount) {
			throw new PaymentCancelException("Cancellation amount is greater than remain amount.");
		}
		boolean isCompleteCancel = paymentCancelRequest.getAmount() + canceledAmount == payment.getAmount();
		long cancelVat = paymentCancelRequest.getVat() == null ? (long)(paymentCancelRequest.getAmount() / 11) : paymentCancelRequest.getVat().longValue();
		if (isCompleteCancel) {
			if(paymentCancelRequest.getVat() == null) {
				cancelVat = payment.getVat() - canceledVat;
			} else if(payment.getVat() != cancelVat + canceledVat) {
				throw new PaymentCancelException("All payments have been cancelled, but VAT remains.");
			}
		} else {
			if (payment.getVat() < cancelVat + canceledVat) {
				throw new PaymentCancelException("Cancel vat is greater than remain vat");
			}
		}

		Payment paymentCancel = Payment.builder()
				.card(payment.getCard())
				.installment(0)
				.amount(paymentCancelRequest.getAmount())
				.vat(cancelVat)
				.orgPaymentId(paymentCancelRequest.getOrgPaymentId())
				.build();
		repository.save(paymentCancel);
		
		return PaymentUpdateResponse.builder()
				.paymentId(paymentCancel.getPaymentId())
				.paymentMessage(paymentCancel.getMessage())
				.build();
	}
}
