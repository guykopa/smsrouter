package com.smsrouter.domain.model;

import org.springframework.lang.NonNull;

/**
 * Immutable result of a routing and delivery attempt.
 *
 * @param operator  operator to which the SMS was routed
 * @param status    final delivery status
 * @param latencyMs time taken to deliver in milliseconds
 */
public record RoutingResult(
        @NonNull Operator operator,
        @NonNull SmsStatus status,
        long latencyMs
) {}
