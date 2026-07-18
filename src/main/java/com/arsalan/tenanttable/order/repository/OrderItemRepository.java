package com.arsalan.tenanttable.order.repository;

import com.arsalan.tenanttable.menu.entity.MenuItem;
import com.arsalan.tenanttable.order.entity.Order;
import com.arsalan.tenanttable.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    //TODO  NOTE: Only add a repository method when a service genuinely needs it

    List<OrderItem> findByOrder(Order order);

    Optional<OrderItem> findByIdAndOrder(UUID id, Order order);

    Optional<OrderItem> findByOrderAndMenuItem(Order order, MenuItem menuItem);

    void deleteByIdAndOrder(UUID id, Order order);

    long countByOrder(Order order);
}
