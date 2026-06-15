package com.honya.bookstore.order.infrastructure.payment;

import com.honya.bookstore.order.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VnPayUrlBuilder {

    private static final DateTimeFormatter VNP_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final int ORDER_ID_LENGTH = 36;

    private final VnPayProperties properties;
    private final VnPaySigner signer;

    /**
     * VNPay rejects a reused vnp_TxnRef, so each attempt gets a unique suffix.
     * The order id is the fixed-length UUID prefix; this recovers it on callback.
     */
    public static UUID extractOrderId(String txnRef) {
        String orderId = (txnRef != null && txnRef.length() >= ORDER_ID_LENGTH)
                ? txnRef.substring(0, ORDER_ID_LENGTH)
                : txnRef;
        return UUID.fromString(orderId);
    }

    public String buildPaymentUrl(Order order, String clientIp, String requestedReturnUrl) {
        // VNPay must redirect to our backend return endpoint so payment status is confirmed
        // server-side; the handler then forwards the browser to the storefront success page.
        String returnUrl = properties.getPaymentReturnUrl();

        OffsetDateTime now = OffsetDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", properties.getTmnCode());
        params.put("vnp_Amount", String.valueOf(order.getTotalAmount() * 100));
        params.put("vnp_CreateDate", now.format(VNP_TIME));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_IpAddr", clientIp);
        params.put("vnp_Locale", "vn");
        params.put("vnp_OrderInfo", "Thanh toan don hang " + order.getId());
        params.put("vnp_OrderType", "other");
        params.put("vnp_ReturnUrl", returnUrl);
        params.put("vnp_TxnRef", order.getId() + "-" + now.toInstant().toEpochMilli());

        String hashData = toQueryString(params);
        String secureHash = signer.hmacSha512(properties.getHashSecret(), hashData);

        return properties.getPayUrl() + "?" + hashData + "&vnp_SecureHash=" + secureHash;
    }

    public boolean isValidSignature(Map<String, String> params) {
        String receivedHash = params.get("vnp_SecureHash");
        if (receivedHash == null || receivedHash.isBlank()) {
            return false;
        }

        Map<String, String> signingParams = new TreeMap<>(params);
        signingParams.remove("vnp_SecureHash");
        signingParams.remove("vnp_SecureHashType");

        String hashData = toQueryString(signingParams);
        String expectedHash = signer.hmacSha512(properties.getHashSecret(), hashData);
        return expectedHash.equalsIgnoreCase(receivedHash);
    }

    private String toQueryString(Map<String, String> params) {
        return params.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isBlank())
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.US_ASCII);
    }
}
