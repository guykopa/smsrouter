package com.smsrouter.domain.model;

import org.springframework.lang.NonNull;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Immutable domain event produced at each step of the SMS lifecycle.
 *
 * @param eventType  type of domain event
 * @param smsId      identifier of the originating SMS message
 * @param timestamp  time the event was produced
 * @param payload    additional event data (operator, status, latency, etc.)
 */
public record SmsEvent(
        @NonNull SmsEventType eventType,
        @NonNull UUID smsId,
        @NonNull Instant timestamp,
        @NonNull Map<String, Object> payload
) {}
