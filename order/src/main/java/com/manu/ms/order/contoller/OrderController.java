package com.manu.ms.order.contoller;

import com.manu.ms.order.dto.OrderRequest;
import com.manu.ms.order.response.SuccessMessage;
import com.manu.ms.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.manu.ms.order.entity.Order;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public CompletableFuture<String> placeOrder(@RequestBody OrderRequest orderRequest) {
//        log.info("Placing Order");
//        return CompletableFuture.supplyAsync(() -> orderService.placeOrder(orderRequest));
//    }

    @PostMapping
    public ResponseEntity<SuccessMessage> placeOrder(@RequestBody OrderRequest orderRequest) {
        log.info("Placing Order");
        String orderResponse = orderService.placeOrder(orderRequest);
        SuccessMessage<String> successMessage = SuccessMessage.<String>builder()
                .code(String.valueOf(HttpStatus.CREATED.value()))
                .message("Order placed successfully")
                .timestamp(java.time.Instant.now().toString())
                .traceId(java.util.UUID.randomUUID().toString())
                .data(orderResponse)
                .build();
        return new ResponseEntity<>(successMessage, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<SuccessMessage> getOrders(Pageable pageable) {
        Page<Order> orders = orderService.getOrders(pageable);
        SuccessMessage<Page<Order>> successMessage = SuccessMessage.<Page<Order>>builder()
                .code(String.valueOf(HttpStatus.OK.value()))
                .message("Orders retrieved successfully")
                .timestamp(Instant.now().toString())
                .traceId(UUID.randomUUID().toString())
                .data(orders)
                .build();
        return ResponseEntity.ok(successMessage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessMessage> deleteOrder(@PathVariable("id") Long id) {
        String result = orderService.deleteOrder(id);
        SuccessMessage<String> successMessage = SuccessMessage.<String>builder()
                .code(String.valueOf(HttpStatus.OK.value()))
                .message("Order deleted successfully")
                .timestamp(Instant.now().toString())
                .traceId(UUID.randomUUID().toString())
                .data(result)
                .build();
        return ResponseEntity.ok(successMessage);
    }
}