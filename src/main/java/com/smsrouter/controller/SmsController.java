package com.smsrouter.controller;

import com.smsrouter.application.SendSmsUseCase;
import com.smsrouter.domain.exception.UnroutableSmsException;
import com.smsrouter.domain.model.RoutingResult;
import com.smsrouter.domain.model.SmsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * REST inbound adapter for SMS submission.
 * Handles HTTP concerns only — no business logic.
 */
@RestController
@RequestMapping("/api/sms")
public class SmsController {

    private static final Logger log = LoggerFactory.getLogger(SmsController.class);

    private final SendSmsUseCase sendSmsUseCase;

    /**
     * @param sendSmsUseCase use case for sending an SMS
     */
    public SmsController(@NonNull SendSmsUseCase sendSmsUseCase) {
        this.sendSmsUseCase = sendSmsUseCase;
    }

    /**
     * Submit an SMS for routing and delivery.
     *
     * @param request map containing "from", "to", and "text" fields
     * @return 202 ACCEPTED with id, status, operator and latency;
     *         422 UNPROCESSABLE_ENTITY if no operator matches the prefix
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> send(
            @RequestBody @NonNull Map<String, String> request) {

        SmsMessage message = new SmsMessage(
                UUID.randomUUID(),
                request.get("from"),
                request.get("to"),
                request.get("text"),
                Instant.now()
        );

        try {
            RoutingResult result = sendSmsUseCase.send(message);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                    "id",        message.id().toString(),
                    "status",    result.status().name(),
                    "operator",  result.operator().name(),
                    "latencyMs", result.latencyMs()
            ));
        } catch (UnroutableSmsException e) {
            log.warn("Unroutable SMS to {}: {}", message.to(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of(
                    "error",       "Unroutable",
                    "phoneNumber", e.getPhoneNumber()
            ));
        }
    }
}
