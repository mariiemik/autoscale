package com.example.payment_service.repository;

import com.example.payment_service.model.PaymentModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentModel, String> {

    PaymentModel findByOrderId(String strings);
}
