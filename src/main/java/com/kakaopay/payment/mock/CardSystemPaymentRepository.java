package com.kakaopay.payment.mock;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kakaopay.payment.entity.Payment;

@Repository
public interface CardSystemPaymentRepository extends JpaRepository<Payment, String> {
	public List<Payment> findByOrgPaymentId(String paymentId);
}
