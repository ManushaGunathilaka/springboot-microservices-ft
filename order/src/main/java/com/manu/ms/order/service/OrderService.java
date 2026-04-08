package com.manu.ms.order.service;

import com.manu.ms.order.client.InventoryClient;
import com.manu.ms.order.dto.OrderRequest;
import com.manu.ms.order.event.OrderPlacedEvent;
import com.manu.ms.order.entity.Order;
import com.manu.ms.order.exception.NotFoundException;
import com.manu.ms.order.exception.OutOfStockException;
import com.manu.ms.order.repositorty.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    //inject KafkaTemplate to publish events to Kafka
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

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


            // Publish an event to notify other services about the new order
            if (orderRequest.getUserDetails() != null) {

                OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent();
                orderPlacedEvent.setOrderNumber(savedOrder.getOrderNumber());
                orderPlacedEvent.setEmail(orderRequest.getUserDetails().email());
                orderPlacedEvent.setFirstName(orderRequest.getUserDetails().firstName());
                orderPlacedEvent.setLastName(orderRequest.getUserDetails().lastName());

                log.info("Start - Sending OrderPlacedEvent {} to Kafka topic 'order-placed'", orderPlacedEvent);
                kafkaTemplate.send("order-placed", orderPlacedEvent);
                log.info("End - OrderPlacedEvent {} sent to Kafka topic 'order-placed'", orderPlacedEvent);

            } else {
                log.warn("User details not available for order {}", order.getOrderNumber());
            }

            return "Order ID: " + savedOrder.getId() + ", Order Number: " + savedOrder.getOrderNumber();
        } else {
            throw new OutOfStockException(
                    "Product with SKU code " + orderRequest.getSkuCode() + " is out of stock.");
        }

    }

    public Page<Order> getOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public String deleteOrder(Long id) {
        return orderRepository.findById(id).map(order -> {
            orderRepository.delete(order);
            log.info("Order {} deleted", id);
            return "Order deleted successfully";
        }).orElseThrow(() -> new NotFoundException("Order with id " + id + " not found."));
    }
}