package com.arsalan.tenanttable.order.service;

import com.arsalan.tenanttable.order.entity.Order;

public interface IOrderCalculationService {

    void calculateTotal(Order order);
}
