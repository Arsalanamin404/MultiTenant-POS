package com.arsalan.tenanttable.order.service;

import com.arsalan.tenanttable.order.entity.Order;

import java.math.BigDecimal;

public interface IOrderCalculationService {

    BigDecimal calculateSubTotal(Order order);

    void calculateTotal(Order order);
}
