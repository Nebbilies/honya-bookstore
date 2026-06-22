package com.honya.bookstore.order.infrastructure.payment;

import com.honya.bookstore.order.domain.Order;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VnPayUrlBuilderTest {

    private static final String SECRET = "FR0LY571CWT5OJGW3C0PGG5A13BUXQ26";

    private VnPayUrlBuilder builder() {
        VnPayProperties properties = new VnPayProperties();
        properties.setTmnCode("8GOFL08H");
        properties.setHashSecret(SECRET);
        properties.setPayUrl("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html");
        properties.setReturnUrl("http://localhost:3000/checkout/payment/success");
        properties.setPaymentReturnUrl("http://localhost:8081/api/orders/payment/vnpay/return");
        return new VnPayUrlBuilder(properties, new VnPaySigner());
    }

    private String paramValue(String url, String key) {
        String query = url.substring(url.indexOf('?') + 1);
        for (String pair : query.split("&")) {
            int eq = pair.indexOf('=');
            if (eq > 0 && pair.substring(0, eq).equals(key)) {
                return pair.substring(eq + 1);
            }
        }
        return null;
    }

    @Test
    void signatureMatchesHashDataAndEncodesSpacesAsPlus() {
        VnPaySigner signer = new VnPaySigner();
        Order order = Order.builder().id(UUID.randomUUID()).totalAmount(150_000).build();

        String url = builder().buildPaymentUrl(order, "127.0.0.1", null);

        String query = url.substring(url.indexOf('?') + 1);
        int hashAt = query.indexOf("&vnp_SecureHash=");
        String hashData = query.substring(0, hashAt);
        String secureHash = query.substring(hashAt + "&vnp_SecureHash=".length());

        // vnp_OrderInfo carries spaces: VNPay encodes them as '+', never '%20'.
        assertTrue(hashData.contains("Thanh+toan+don+hang"));
        assertFalse(hashData.contains("%20"));

        // The transmitted hash must equal HMAC over the exact transmitted query string.
        assertEquals(signer.hmacSha512(SECRET, hashData), secureHash);
    }

    @Test
    void txnRefCarriesUniqueSuffixButRecoversOrderId() {
        UUID orderId = UUID.randomUUID();
        Order order = Order.builder().id(orderId).totalAmount(150_000).build();

        String txnRef = paramValue(builder().buildPaymentUrl(order, "127.0.0.1", null), "vnp_TxnRef");

        // VNPay rejects a reused ref, so it must differ from the bare order id...
        assertTrue(txnRef.length() > 36);
        assertTrue(txnRef.startsWith(orderId.toString()));
        // ...yet still resolve back to the order on callback.
        assertEquals(orderId, VnPayUrlBuilder.extractOrderId(txnRef));
    }

    @Test
    void extractOrderIdHandlesSuffixedAndBareRefs() {
        UUID orderId = UUID.randomUUID();
        assertEquals(orderId, VnPayUrlBuilder.extractOrderId(orderId + "-1718000000000"));
        assertEquals(orderId, VnPayUrlBuilder.extractOrderId(orderId.toString()));
    }
}
