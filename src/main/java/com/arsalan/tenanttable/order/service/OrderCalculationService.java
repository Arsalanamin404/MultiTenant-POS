package com.arsalan.tenanttable.order.service;

import com.arsalan.tenanttable.coupon.entity.Coupon;
import com.arsalan.tenanttable.coupon.enums.CouponType;
import com.arsalan.tenanttable.order.entity.Order;
import com.arsalan.tenanttable.order.entity.OrderItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class OrderCalculationService implements IOrderCalculationService {

    @Override
    public BigDecimal calculateSubTotal(Order order) {
        log.debug("Calculation SUB_TOTAL for order number: #{}", order.getOrderNumber());

        return order.getItems()
                .stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateDiscount(BigDecimal subtotal, Coupon coupon) {
        if (coupon == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;

        if (coupon.getCouponType() == CouponType.PERCENTAGE) {

            discount = subtotal
                    .multiply(coupon.getValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (coupon.getMaximumDiscount() != null
                    && discount.compareTo(coupon.getMaximumDiscount()) > 0) {
                discount = coupon.getMaximumDiscount();
            }

        } else {
            discount = coupon.getValue();
            if (discount.compareTo(subtotal) > 0) {
                discount = subtotal;
            }
        }

        return discount;
    }

    private BigDecimal calculateTax(BigDecimal subtotal, BigDecimal taxRate) {
        return subtotal
                .multiply(taxRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    @Override
    public void calculateTotal(Order order) {
        log.debug("Calculation TOTAL_ORDER_AMOUNT for order number: #{}", order.getOrderNumber());

        BigDecimal subtotal = calculateSubTotal(order);
        BigDecimal discount = calculateDiscount(subtotal, order.getCoupon());
        BigDecimal tax = calculateTax(subtotal, order.getTaxRate());

        BigDecimal total = subtotal
                .subtract(discount)
                .add(tax);

        log.debug(
                "Order #{} calculated. subtotal={}, discount={}, tax={}, total={}",
                order.getOrderNumber(),
                subtotal,
                discount,
                tax,
                total
        );


        order.setSubTotal(subtotal);
        order.setTaxAmount(tax);
        order.setTotalAmount(total);
    }
}
