package com.kakaopay.payment.service;

import static org.hamcrest.Matchers.hasLength;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.payment.entity.Card;
import com.kakaopay.payment.entity.Payment;
import com.kakaopay.payment.entity.dto.PaymentCancelRequest;
import com.kakaopay.payment.entity.dto.PaymentRequest;
import com.kakaopay.payment.entity.dto.PaymentUpdateResponse;
import com.kakaopay.payment.mock.CardSystemPaymentRepository;
import com.kakaopay.payment.util.IDUtil;
import com.kakaopay.payment.util.PaymentStringUtils;

@SpringBootTest
@AutoConfigureMockMvc
@Rollback(false)
public class PaymentControllerTest {
	@Autowired
	private CardSystemPaymentRepository cardSystemPaymentRepository;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Test
	public void testUniqueId() {
		Set<String> ids = new HashSet<String>();
		int limit = 5000;
		for(int i = 0; i < limit; i++) {
			String id = IDUtil.uniqueId(20);
			assertEquals(id.length(), 20);
			ids.add(id);
			
		}
		assertEquals(ids.size(), limit);
	}
	
	@Test
	public void testGetPayment() throws Exception {
		String cardNo = "1234567890123456";
		String expire = "0122";
		String cvc = "010";
		
		int installment = 1;
		int amount = 10000;
		int vat = 1100;
		
		Card card = Card.builder()
				.cardNo(cardNo)
				.expire(expire)
				.cvc(cvc)
				.build();
		Payment payment = Payment.builder()
				.card(card)
				.installment(installment)
				.amount(amount)
				.vat(vat)
				.build();
		cardSystemPaymentRepository.save(payment);
		
		mockMvc.perform(
				get("/v1/payments/" + payment.getPaymentId()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.cardNo").value(PaymentStringUtils.getCardMasking(cardNo)))
		.andExpect(jsonPath("$.paymentDivision").value("PAYMENT"))
		.andDo(print());
	}
	
	@Test
	public void testGetPaymentNoResource() throws Exception {
		mockMvc.perform(
				get("/v1/payments/errorPaymentId"))
		.andExpect(status().is(400))
		.andExpect(jsonPath("$.code").value(-2000))
		.andExpect(jsonPath("$.message").value("Cannot found payment"))
		.andDo(print());
	}
	
	@Test
	public void testSavePayment() throws Exception {
		PaymentRequest request = PaymentRequest.builder()
				.cardNo("1234567890123456")
				.installment(0)
			    .expire("0122")
			    .cvc("030")
			    .amount(100000)
			    .vat(11000L)
				.build();
		
		String requestContent = objectMapper.writeValueAsString(request);
		
		mockMvc.perform(
				post("/v1/payments")
				.content(requestContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.paymentId").exists())
		.andExpect(jsonPath("$.paymentMessage", hasLength(450)))
		.andDo(print());
	}
	
	@Test
	public void testSavePaymentBadRequest() throws Exception {
		PaymentRequest request = PaymentRequest.builder()
				.cardNo("abcd")
				.installment(-1)
			    .expire("122")
			    .cvc("abd")
			    .amount(-100000)
			    .vat(-11000L)
				.build();
		
		String requestContent = objectMapper.writeValueAsString(request);
		
		mockMvc.perform(
				post("/v1/payments")
				.content(requestContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400))
		.andExpect(jsonPath("$.code").value(-1002))
		.andExpect(jsonPath("$.message").value("Invalid input"))
		.andDo(print());
	}
	
	@Test
	public void testSavePaymentNoVat() throws Exception {
		PaymentRequest request = PaymentRequest.builder()
				.cardNo("1234567890123456")
				.installment(0)
			    .expire("0122")
			    .cvc("030")
			    .amount(150000)
				.build();
		long expectVat = (long)(request.getAmount() / 11);
		
		String requestContent = objectMapper.writeValueAsString(request);
		
		MvcResult postResult = mockMvc.perform(
				post("/v1/payments")
				.content(requestContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.paymentId").exists())
		.andExpect(jsonPath("$.paymentMessage", hasLength(450)))
		.andDo(print())
		.andReturn();

        ObjectMapper mapper = new ObjectMapper();
        PaymentUpdateResponse responseObject = mapper.readValue(postResult.getResponse().getContentAsString(), PaymentUpdateResponse.class);
        
		mockMvc.perform(
				get("/v1/payments/" + responseObject.getPaymentId()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.vat").value(expectVat))
		.andDo(print());
	}
	
	@Test
	public void testSavePaymentCancel() throws Exception {
		PaymentRequest request = PaymentRequest.builder()
				.cardNo("1234567890123456")
				.installment(0)
			    .expire("0122")
			    .cvc("030")
			    .amount(100000)
				.build();
		Card card = Card.builder()
				.cardNo(request.getCardNo())
				.cvc(request.getCvc())
				.expire(request.getExpire())
				.build();
		Payment payment = Payment.builder()
				.card(card)
				.installment(request.getInstallment())
				.amount(request.getAmount())
				.vat(request.getVat() == null ? (long)(request.getAmount() / 11) : request.getVat().longValue())
				.build();
		cardSystemPaymentRepository.save(payment);
		
		PaymentCancelRequest cancelRequest = PaymentCancelRequest.builder()
				.orgPaymentId(payment.getPaymentId())
			    .amount(request.getAmount())
				.build();
		
		String content = objectMapper.writeValueAsString(cancelRequest);
		
		mockMvc.perform(
				post("/v1/payments/cancel")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.paymentId").exists())
		.andExpect(jsonPath("$.paymentMessage", hasLength(450)))
		.andDo(print());		
	}
	

	@Test
	public void testSavePaymentPartialCancel() throws Exception {
		PaymentRequest request = PaymentRequest.builder()
				.cardNo("1234567890123456")
				.installment(0)
			    .expire("0122")
			    .cvc("030")
			    .amount(100000)
				.build();
		Card card = Card.builder()
				.cardNo(request.getCardNo())
				.cvc(request.getCvc())
				.expire(request.getExpire())
				.build();
		Payment payment = Payment.builder()
				.card(card)
				.installment(request.getInstallment())
				.amount(request.getAmount())
				.vat(request.getVat() == null ? (long)(request.getAmount() / 11) : request.getVat().longValue())
				.build();
		cardSystemPaymentRepository.save(payment);
		
		PaymentCancelRequest cancelRequest = PaymentCancelRequest.builder()
				.orgPaymentId(payment.getPaymentId())
			    .amount(40000)
			    .vat(5000L)
				.build();
		
		String content = objectMapper.writeValueAsString(cancelRequest);
		
		mockMvc.perform(
				post("/v1/payments/cancel")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.paymentId").exists())
		.andExpect(jsonPath("$.paymentMessage", hasLength(450)))
		.andDo(print());		
		
		cancelRequest = PaymentCancelRequest.builder()
				.orgPaymentId(payment.getPaymentId())
			    .amount(request.getAmount() - cancelRequest.getAmount())
				.build();
		
		content = objectMapper.writeValueAsString(cancelRequest);
		
		mockMvc.perform(
				post("/v1/payments/cancel")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.paymentId").exists())
		.andExpect(jsonPath("$.paymentMessage", hasLength(450)))
		.andDo(print());		
	}
	

	@Test
	public void testSavePaymentPartialCancelFailed() throws Exception {
		PaymentRequest request = PaymentRequest.builder()
				.cardNo("1234567890123456")
				.installment(0)
			    .expire("0122")
			    .cvc("030")
			    .amount(100000)
				.build();
		Card card = Card.builder()
				.cardNo(request.getCardNo())
				.cvc(request.getCvc())
				.expire(request.getExpire())
				.build();
		Payment payment = Payment.builder()
				.card(card)
				.installment(request.getInstallment())
				.amount(request.getAmount())
				.vat(request.getVat() == null ? (long)(request.getAmount() / 11) : request.getVat().longValue())
				.build();
		cardSystemPaymentRepository.save(payment);
		
		PaymentCancelRequest cancelRequest = PaymentCancelRequest.builder()
				.orgPaymentId(payment.getPaymentId())
			    .amount(40000)
			    .vat(5000L)
				.build();
		
		String content = objectMapper.writeValueAsString(cancelRequest);
		
		mockMvc.perform(
				post("/v1/payments/cancel")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.paymentId").exists())
		.andExpect(jsonPath("$.paymentMessage", hasLength(450)))
		.andDo(print());		
		
		cancelRequest = PaymentCancelRequest.builder()
				.orgPaymentId(payment.getPaymentId())
			    .amount(request.getAmount() - cancelRequest.getAmount())
			    .vat(3000L)
				.build();
		
		content = objectMapper.writeValueAsString(cancelRequest);
		
		mockMvc.perform(
				post("/v1/payments/cancel")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400))
		.andExpect(jsonPath("$.code").value(-3000))
		.andExpect(jsonPath("$.detailMessage").value("All payments have been cancelled, but VAT remains."))
		.andDo(print());		
		
		cancelRequest = PaymentCancelRequest.builder()
				.orgPaymentId(payment.getPaymentId())
			    .amount(request.getAmount() - 30000)
			    .vat(10000L)
				.build();
		
		content = objectMapper.writeValueAsString(cancelRequest);
		
		mockMvc.perform(
				post("/v1/payments/cancel")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400))
		.andExpect(jsonPath("$.code").value(-3000))
		.andExpect(jsonPath("$.detailMessage").value("Cancellation amount is greater than remain amount."))
		.andDo(print());		
		
		cancelRequest = PaymentCancelRequest.builder()
				.orgPaymentId(payment.getPaymentId())
			    .amount(10000)
			    .vat(10000L)
				.build();
		
		content = objectMapper.writeValueAsString(cancelRequest);
		
		mockMvc.perform(
				post("/v1/payments/cancel")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400))
		.andExpect(jsonPath("$.code").value(-3000))
		.andExpect(jsonPath("$.detailMessage").value("Cancel vat is greater than remain vat"))
		.andDo(print());	
	}
	
	@Test
	public void testCancelBadRequest() throws Exception {
		PaymentRequest request = PaymentRequest.builder()
				.cardNo("abcd")
				.installment(-1)
			    .expire("122")
			    .cvc("abd")
			    .amount(-100000)
			    .vat(-11000L)
				.build();
		
		String requestContent = objectMapper.writeValueAsString(request);
		
		mockMvc.perform(
				post("/v1/payments")
				.content(requestContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().is(400))
		.andExpect(jsonPath("$.code").value(-1002))
		.andExpect(jsonPath("$.message").value("Invalid input"))
		.andDo(print());
	}
}
