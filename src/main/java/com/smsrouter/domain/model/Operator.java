package com.smsrouter.domain.model;

import org.springframework.lang.NonNull;

/**
 * Immutable representation of a mobile network operator.
 *
 * @param name     operator commercial name
 * @param prefix   E.164 country/network prefix (e.g. "+44")
 * @param country  ISO country code or name
 * @param priority routing priority — lower value = higher priority
 */
public record Operator(
        @NonNull String name,
        @NonNull String prefix,
        @NonNull String country,
        int priority
) {}
