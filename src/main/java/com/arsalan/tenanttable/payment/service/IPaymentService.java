package com.arsalan.tenanttable.payment.service;

import com.arsalan.tenanttable.payment.dto.CreatePaymentRequestDto;
import com.arsalan.tenanttable.payment.dto.PaymentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IPaymentService {
    PaymentResponseDto create(CreatePaymentRequestDto dto);

    PaymentResponseDto getById(UUID id);

    PaymentResponseDto getByOrderId(UUID orderId);

    Page<PaymentResponseDto> getAll(Pageable pageable);

}
