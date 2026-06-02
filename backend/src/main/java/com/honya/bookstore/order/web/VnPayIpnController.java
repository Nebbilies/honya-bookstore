package com.honya.bookstore.order.web;

import com.honya.bookstore.order.application.OrderService;
import com.honya.bookstore.order.domain.Order;
import com.honya.bookstore.order.infrastructure.payment.VnPayUrlBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Tag(name = "VNPay", description = "Endpoints for VNPay payment callback")
@RestController
@RequestMapping("/api/orders/payment/vnpay")
@RequiredArgsConstructor
public class VnPayIpnController {

    private final OrderService orderService;
    private final VnPayUrlBuilder vnPayUrlBuilder;

    @Operation(summary = "VNPay IPN", description = "Receive VNPay server callback and update payment status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "IPN acknowledged")
    })
    @GetMapping("/ipn")
    public ResponseEntity<Map<String, String>> ipn(@RequestParam Map<String, String> queryParams) {
        if (!vnPayUrlBuilder.isValidSignature(queryParams)) {
            return ResponseEntity.ok(vnpResponse("97", "Invalid signature"));
        }

        String txnRef = queryParams.get("vnp_TxnRef");
        String transactionNo = queryParams.get("vnp_TransactionNo");
        String responseCode = queryParams.get("vnp_ResponseCode");
        long receivedAmount = parseAmount(queryParams.get("vnp_Amount"));

        UUID orderId;
        try {
            orderId = UUID.fromString(txnRef);
        } catch (Exception ex) {
            return ResponseEntity.ok(vnpResponse("01", "Order not found"));
        }

        Order order;
        try {
            order = orderService.getOrderById(orderId);
        } catch (Exception ex) {
            return ResponseEntity.ok(vnpResponse("01", "Order not found"));
        }

        long expectedAmount = (long) order.getTotalAmount() * 100;
        if (receivedAmount != expectedAmount) {
            return ResponseEntity.ok(vnpResponse("04", "Invalid amount"));
        }

        if (Boolean.TRUE.equals(order.getIsPaid())) {
            return ResponseEntity.ok(vnpResponse("00", "Confirm Success"));
        }

        if ("00".equals(responseCode)) {
            orderService.updatePaymentStatus(orderId, true, transactionNo, "PROCESSING");
        } else {
            orderService.updatePaymentStatus(orderId, false, transactionNo, "CANCELLED");
        }

        return ResponseEntity.ok(vnpResponse("00", "Confirm Success"));
    }

    private Map<String, String> vnpResponse(String code, String message) {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("RspCode", code);
        response.put("Message", message);
        return response;
    }

    private long parseAmount(String amount) {
        if (amount == null || amount.isBlank()) {
            return 0;
        }
        try {
            return Long.parseLong(amount);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
