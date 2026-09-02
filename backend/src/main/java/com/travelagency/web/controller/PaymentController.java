package com.travelagency.web.controller;

import com.travelagency.common.api.ApiResponse;
import com.travelagency.common.exception.BusinessException;
import com.travelagency.domain.dto.PaymentCallbackRequest;
import com.travelagency.domain.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final OrderService orderService;
    private final String callbackSecret;

    public PaymentController(
            OrderService orderService,
            @Value("${app.integrations.alipay.callback-secret:travel-agency-sandbox-callback-secret}") String callbackSecret) {
        this.orderService = orderService;
        this.callbackSecret = callbackSecret;
    }

    @PostMapping("/alipay/callback")
    public ApiResponse<Void> callback(@Valid @RequestBody PaymentCallbackRequest request) {
        if (!"SUCCESS".equalsIgnoreCase(request.result()) || !verify(request)) {
            throw new BusinessException(400, "支付回调验签失败");
        }
        orderService.markPaid(request.orderNo(), request.tradeNo());
        return ApiResponse.ok();
    }

    private boolean verify(PaymentCallbackRequest request) {
        if (request.signature() == null || request.signature().isBlank()) {
            return false;
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(callbackSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] expected = mac.doFinal((request.orderNo() + "|" + request.tradeNo() + "|" + request.result())
                    .getBytes(StandardCharsets.UTF_8));
            byte[] actual = Base64.getDecoder().decode(request.signature());
            return MessageDigest.isEqual(expected, actual);
        } catch (Exception ex) {
            return false;
        }
    }
}
