package com.smsrouter.fixture;

import com.smsrouter.domain.model.Operator;
import com.smsrouter.domain.model.RoutingResult;
import com.smsrouter.domain.model.SmsMessage;
import com.smsrouter.domain.model.SmsStatus;
import com.smsrouter.port.SmsDeliveryPort;
import org.springframework.lang.NonNull;

/**
 * Fake SMS delivery adapter for unit tests.
 * Returns DELIVERED by default; can be configured to fail.
 */
public class FakeSmsDelivery implements SmsDeliveryPort {

    private final boolean shouldFail;

    /** Creates a fake delivery that always succeeds. */
    public FakeSmsDelivery() {
        this(false);
    }

    /**
     * @param shouldFail when true, every delivery returns FAILED
     */
    public FakeSmsDelivery(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    @Override
    public RoutingResult deliver(@NonNull SmsMessage message, @NonNull Operator operator) {
        SmsStatus status = shouldFail ? SmsStatus.FAILED : SmsStatus.DELIVERED;
        return new RoutingResult(operator, status, 10L);
    }
}
