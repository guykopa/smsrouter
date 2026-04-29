package com.smsrouter.domain.model;

import org.springframework.lang.NonNull;
import java.time.Instant;
import java.util.UUID;

/**
 * Immutable representation of an SMS message entering the routing engine.
 *
 * @param id        unique message identifier
 * @param from      E.164 sender phone number
 * @param to        E.164 recipient phone number
 * @param text      message body
 * @param timestamp time the message was received
 */
public record SmsMessage(
        @NonNull UUID id,
        @NonNull String from,
        @NonNull String to,
        @NonNull String text,
        @NonNull Instant timestamp
) {}
