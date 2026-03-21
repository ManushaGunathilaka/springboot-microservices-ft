package com.manu.ms.order.service;

import com.manu.ms.order.client.InventoryClient;
import com.manu.ms.order.dto.OrderRequest;
import com.manu.ms.order.model.Order;
import com.manu.ms.order.repositorty.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public String placeOrder(OrderRequest orderRequest) {

        var isProductInStock = inventoryClient.isInStock(orderRequest.getSkuCode(), orderRequest.getQuantity());

        if(isProductInStock) {
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setSkuCode(orderRequest.getSkuCode());
            order.setPrice(orderRequest.getPrice());
            order.setQuantity(orderRequest.getQuantity());
            Order savedOrder = orderRepository.save(order);
            log.info("Order {} placed successfully with id: {}", savedOrder.getOrderNumber(), savedOrder.getId());
            return "Order ID: " + savedOrder.getId() + ", Order Number: " + savedOrder.getOrderNumber();
        }else {
            throw new RuntimeException("Product with SKU code " + orderRequest.getSkuCode() + " is out of stock.");
        }

    }
}