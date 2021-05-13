package com.kakaopay.payment.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.payment.entity.dto.PaymentCancelRequest;
import com.kakaopay.payment.entity.dto.PaymentRequest;
import com.kakaopay.payment.entity.dto.PaymentUpdateResponse;

@SpringBootTest
@AutoConfigureMockMvc
@Rollback(false)
public class PaymentHomeworkTest {
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	public PaymentCancelRequest getCancelRequest(String paymentId, long amount, Long vat) {
		return PaymentCancelRequest.builder()
				.orgPaymentId(paymentId)
			    .amount(amount)
			    .vat(vat)
				.build();
	}
	
	public String postPayment(long amount, Long vat) throws Exception {
		PaymentRequest request = PaymentRequest.builder()
				.cardNo("1234567890123456")
				.installment(0)
			    .expire("0122")
			    .cvc("030")
			    .amount(amount)
			    .vat(vat)
				.build();
		String requestContent = objectMapper.writeValueAsString(request);
		
		MvcResult postResult = mockMvc.perform(
				post("/v1/payments")
				.content(requestContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andReturn();

        PaymentUpdateResponse responseObject = objectMapper.readValue(postResult.getResponse().getContentAsString(), PaymentUpdateResponse.class);;
        return responseObject.getPaymentMessage().substring(14, 34);
	}
	
	@Test
	public void testCase1() throws Exception {
		String paymentId = postPayment(11000, 1000L);
        String requestContent = objectMapper.writeValueAsString(getCancelRequest(paymentId, 1100, 100L));
		
		mockMvc.perform(
				post("/v1/payments/cancel")
				.content(requestContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
		
		requestContent = objectMapper.writeValueAsString(getCancelRequest(paymentId, 3300, null));
		
		mockMvc.perform(
				post("/v1/payments/cancel")
				.content(requestContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
		
		requestContent = objectMapper.writeValueAsString(getCancelRequest(paymentId, 7000, null));
		
		mockMvc.perform(
				post("/v1/payments/cancel")
				.content(requestContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400));
		
		requestContent = objectMapper.writeValueAsString(getCancelRequest(paymentId, 6600, 700L));
		
		mockMvc.perform(
				post("/v1/payments/cancel")
				.content(requestContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400));
		
		requestContent = objectMapper.writeValueAsString(getCancelRequest(paymentId, 6600, 600L));
		
		mockMvc.perform(
				post("/v1/payments/cancel")
				.content(requestContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
		
		requestContent = objectMapper.writeValueAsString(getCancelRequest(paymentId, 100, null));
		
		mockMvc.perform(
				post("/v1/payments/cancel")
				.content(requestContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400));
	}
	
	@Test
	public void testCase2() throws Exception {
		String paymentId = postPayment(20000, 909L);
		
        String requestContent = objectMapper.writeValueAsString(getCancelRequest(paymentId, 10000, 0L));
		
		mockMvc.perform(
				post("/v1/payments/cancel")
				.content(requestContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
		
		requestContent = objectMapper.writeValueAsString(getCancelRequest(paymentId, 10000, 0L));
		
		mockMvc.perform(
				post("/v1/payments/cancel")
				.content(requestContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400));
		
		requestContent = objectMapper.writeValueAsString(getCancelRequest(paymentId, 10000, 909L));
		
		mockMvc.perform(
				post("/v1/payments/cancel")
				.content(requestContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
	
	@Test
	public void testCase3() throws Exception {
		String paymentId = postPayment(20000, null);
		
        String requestContent = objectMapper.writeValueAsString(getCancelRequest(paymentId, 10000, 1000L));
		
		mockMvc.perform(
				post("/v1/payments/cancel")
				.content(requestContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
		
		requestContent = objectMapper.writeValueAsString(getCancelRequest(paymentId, 10000, 909L));
		
		mockMvc.perform(
				post("/v1/payments/cancel")
				.content(requestContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400));
		
		requestContent = objectMapper.writeValueAsString(getCancelRequest(paymentId, 10000, null));
		
		mockMvc.perform(
				post("/v1/payments/cancel")
				.content(requestContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
}
