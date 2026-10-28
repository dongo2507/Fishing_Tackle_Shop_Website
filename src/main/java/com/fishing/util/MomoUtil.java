package com.fishing.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

/** Thanh toán qua cổng MoMo (môi trường test của MoMo, không trừ tiền thật). */
public final class MomoUtil {

    private static final String ENDPOINT = "https://test-payment.momo.vn/v2/gateway/api/create";
    private static final String PARTNER_CODE = "MOMO";
    private static final String ACCESS_KEY = "F8BBA842ECF85";
    private static final String SECRET_KEY = "K951B6PE1waDMi640xX08PD3vg6EkVlz";
    private static final String REQUEST_TYPE = "payWithMethod";

    private MomoUtil() {}

    /** Gửi đơn sang MoMo, trả về link trang thanh toán có mã QR. */
    public static String createPayment(String orderId, long amount, String orderInfo,
                                       String redirectUrl, String ipnUrl) {
        String requestId = UUID.randomUUID().toString();
        String raw = "accessKey=" + ACCESS_KEY + "&amount=" + amount + "&extraData="
                + "&ipnUrl=" + ipnUrl + "&orderId=" + orderId + "&orderInfo=" + orderInfo
                + "&partnerCode=" + PARTNER_CODE + "&redirectUrl=" + redirectUrl
                + "&requestId=" + requestId + "&requestType=" + REQUEST_TYPE;

        JsonObject body = new JsonObject();
        body.addProperty("partnerCode", PARTNER_CODE);
        body.addProperty("requestId", requestId);
        body.addProperty("amount", amount);
        body.addProperty("orderId", orderId);
        body.addProperty("orderInfo", orderInfo);
        body.addProperty("redirectUrl", redirectUrl);
        body.addProperty("ipnUrl", ipnUrl);
        body.addProperty("requestType", REQUEST_TYPE);
        body.addProperty("extraData", "");
        body.addProperty("lang", "vi");
        body.addProperty("signature", sign(raw));

        JsonObject res;
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(ENDPOINT))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            res = JsonParser.parseString(response.body()).getAsJsonObject();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("Không kết nối được tới MoMo.");
        } catch (IOException | RuntimeException e) {
            throw new BusinessException("Không kết nối được tới MoMo.");
        }

        if (!res.has("resultCode") || res.get("resultCode").getAsInt() != 0 || !res.has("payUrl")) {
            String msg = res.has("message") ? res.get("message").getAsString() : "";
            throw new BusinessException("MoMo từ chối thanh toán: " + msg);
        }
        return res.get("payUrl").getAsString();
    }

    /** Kiểm tra chữ ký kết quả MoMo gửi về, sai chữ ký thì không tin. */
    public static boolean verify(Map<String, String> p) {
        String raw = "accessKey=" + ACCESS_KEY + "&amount=" + get(p, "amount")
                + "&extraData=" + get(p, "extraData") + "&message=" + get(p, "message")
                + "&orderId=" + get(p, "orderId") + "&orderInfo=" + get(p, "orderInfo")
                + "&orderType=" + get(p, "orderType") + "&partnerCode=" + get(p, "partnerCode")
                + "&payType=" + get(p, "payType") + "&requestId=" + get(p, "requestId")
                + "&responseTime=" + get(p, "responseTime") + "&resultCode=" + get(p, "resultCode")
                + "&transId=" + get(p, "transId");
        return MessageDigest.isEqual(sign(raw).getBytes(StandardCharsets.UTF_8),
                get(p, "signature").getBytes(StandardCharsets.UTF_8));
    }

    /** Địa chỉ gốc của web, VD http://localhost:8080 */
    public static String baseUrl(HttpServletRequest req) {
        String proto = req.getHeader("X-Forwarded-Proto");   // chạy trên Render
        String scheme = proto != null ? proto : req.getScheme();
        return scheme + "://" + req.getHeader("Host") + req.getContextPath();
    }

    static String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            StringBuilder hex = new StringBuilder();
            for (byte b : mac.doFinal(data.getBytes(StandardCharsets.UTF_8))) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static String get(Map<String, String> p, String key) {
        String v = p.get(key);
        return v == null ? "" : v;
    }
}
