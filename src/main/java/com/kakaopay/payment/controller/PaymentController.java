package com.kakaopay.payment.controller;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kakaopay.payment.entity.dto.PaymentCancelRequest;
import com.kakaopay.payment.entity.dto.PaymentRequest;
import com.kakaopay.payment.entity.dto.PaymentResponse;
import com.kakaopay.payment.entity.dto.PaymentUpdateResponse;
import com.kakaopay.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1")
public class PaymentController {
//	public HttpEntity<T>
	private final PaymentService paymentService;

	@GetMapping(value="/payments/{id}")
	public PaymentResponse getPaymentById(@PathVariable String id) {
		return paymentService.findPaymentById(id);
	}
	
	@PostMapping(value="/payments/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
	public PaymentUpdateResponse cancelPayment(@RequestBody @Validated PaymentCancelRequest paymentCancelRequest) {
		return paymentService.createPaymentCancel(paymentCancelRequest);
	}
	
	@PostMapping(value="/payments", consumes = MediaType.APPLICATION_JSON_VALUE)
	public PaymentUpdateResponse createPayment(@RequestBody @Validated PaymentRequest paymentRequest) {
		return paymentService.createPayment(paymentRequest);
	}
}
