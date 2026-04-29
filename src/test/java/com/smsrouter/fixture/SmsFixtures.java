package com.smsrouter.fixture;

import com.smsrouter.domain.model.Operator;
import com.smsrouter.domain.model.SmsMessage;

import java.time.Instant;
import java.util.UUID;

/**
 * Shared factory methods for test data.
 */
public final class SmsFixtures {

    private SmsFixtures() {}

    /** SMS from France to UK — routable. */
    public static SmsMessage normalSms() {
        return new SmsMessage(
                UUID.randomUUID(),
                "+33612345678",
                "+447911123456",
                "Hello World",
                Instant.now()
        );
    }

    /** SMS to an unknown prefix — not routable. */
    public static SmsMessage unroutableSms() {
        return new SmsMessage(
                UUID.randomUUID(),
                "+33612345678",
                "+99999999999",
                "Test",
                Instant.now()
        );
    }

    /** EE UK operator, priority 1. */
    public static Operator eeOperator() {
        return new Operator("EE", "+44", "UK", 1);
    }

    /** Orange France operator, priority 1. */
    public static Operator orangeOperator() {
        return new Operator("Orange FR", "+33", "FR", 1);
    }

    /** Deutsche Telekom operator, priority 1. */
    public static Operator deutscheOperator() {
        return new Operator("Deutsche Telekom", "+49", "DE", 1);
    }

    /** AT&T US operator, priority 1. */
    public static Operator attOperator() {
        return new Operator("AT&T", "+1", "US", 1);
    }
}
