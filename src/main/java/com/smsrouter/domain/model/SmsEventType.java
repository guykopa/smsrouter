package com.smsrouter.domain.model;

/**
 * Types of domain events produced during SMS routing lifecycle.
 */
public enum SmsEventType {
    SMS_RECEIVED,
    SMS_ROUTED,
    SMS_DELIVERED,
    SMS_FAILED,
    SMS_RETRY
}
