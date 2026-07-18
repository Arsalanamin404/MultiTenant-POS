package com.arsalan.tenanttable.order.service;

import com.arsalan.tenanttable.order.entity.Order;
import com.arsalan.tenanttable.order.entity.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class OrderCalculationService implements IOrderCalculationService {

    private BigDecimal calculateSubTotal(Order order) {
        log.debug("Calculation SUB_TOTAL for order number: #{}", order.getOrderNumber());

        return order.getItems()
                .stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTax(BigDecimal subtotal, BigDecimal taxRate) {
        return subtotal
                .multiply(taxRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDiscount(BigDecimal subtotal, BigDecimal discountRate) {
        return subtotal
                .multiply(discountRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    @Override
    public void calculateTotal(Order order) {
        log.debug("Calculation TOTAL_ORDER_AMOUNT for order number: #{}", order.getOrderNumber());

        BigDecimal subtotal = calculateSubTotal(order);
        BigDecimal tax = calculateTax(subtotal, order.getTaxRate());
        BigDecimal discount = calculateDiscount(subtotal, order.getDiscountRate());

        BigDecimal total = subtotal
                .add(tax)
                .subtract(discount);

        log.debug(
                "TOTAL_ORDER_AMOUNT for order number: #{} is AMOUNT = {}",
                order.getOrderNumber(),
                total
        );

        order.setSubTotal(subtotal);
        order.setTaxAmount(tax);
        order.setDiscountAmount(discount);
        order.setTotalAmount(total);
    }
}
