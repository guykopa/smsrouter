package com.smsrouter.domain.model;

/**
 * Lifecycle status of an SMS message through the routing engine.
 */
public enum SmsStatus {
    RECEIVED,
    ROUTING,
    DELIVERED,
    FAILED,
    RETRY
}
