package com.smsrouter.adapter.delivery;

import com.smsrouter.domain.model.Operator;
import com.smsrouter.domain.model.RoutingResult;
import com.smsrouter.domain.model.SmsMessage;
import com.smsrouter.domain.model.SmsStatus;
import com.smsrouter.port.SmsDeliveryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Simulated SMS delivery adapter.
 * Adds realistic latency (10–50 ms) and a 10 % failure rate for retry testing.
 */
@Component
public class SimulatedSmsDelivery implements SmsDeliveryPort {

    private static final Logger log = LoggerFactory.getLogger(SimulatedSmsDelivery.class);
    private static final int FAILURE_RATE_PERCENT = 10;

    @Override
    public RoutingResult deliver(@NonNull SmsMessage message, @NonNull Operator operator) {
        long latencyMs = ThreadLocalRandom.current().nextLong(10, 51);
        simulateLatency(latencyMs);

        boolean failed = ThreadLocalRandom.current().nextInt(100) < FAILURE_RATE_PERCENT;
        SmsStatus status = failed ? SmsStatus.FAILED : SmsStatus.DELIVERED;

        log.debug("Delivery to {} — status={} latency={}ms", operator.name(), status, latencyMs);
        return new RoutingResult(operator, status, latencyMs);
    }

    private void simulateLatency(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
