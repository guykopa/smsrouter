package com.smsrouter.domain.service;

import com.smsrouter.domain.model.RoutingResult;
import com.smsrouter.domain.model.SmsEvent;
import com.smsrouter.domain.model.SmsEventType;
import com.smsrouter.domain.model.SmsMessage;
import com.smsrouter.domain.model.Operator;
import com.smsrouter.port.OperatorRegistryPort;
import com.smsrouter.port.SmsDeliveryPort;
import com.smsrouter.port.SmsPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.util.Map;

/**
 * Domain service that resolves the target operator and attempts delivery.
 *
 * <p>Publishes {@link SmsEventType#SMS_ROUTED} after operator resolution.
 * Retry logic lives in the use case, never here.
 */
public class SmsRoutingService {

    private static final Logger log = LoggerFactory.getLogger(SmsRoutingService.class);

    private final OperatorRegistryPort registry;
    private final SmsDeliveryPort delivery;
    private final SmsPublisherPort publisher;

    /**
     * @param registry  operator lookup port
     * @param delivery  delivery attempt port
     * @param publisher domain event publisher port
     */
    public SmsRoutingService(
            @NonNull OperatorRegistryPort registry,
            @NonNull SmsDeliveryPort delivery,
            @NonNull SmsPublisherPort publisher) {
        this.registry = registry;
        this.delivery = delivery;
        this.publisher = publisher;
    }

    /**
     * Resolve the operator and attempt delivery for the given SMS.
     *
     * @param message the SMS to route
     * @return {@link RoutingResult} with operator, status and latency
     * @throws com.smsrouter.domain.exception.UnroutableSmsException if no operator matches
     */
    public RoutingResult route(@NonNull SmsMessage message) {
        Operator operator = registry.findByPrefix(message.to());
        log.debug("Routing SMS {} to operator {}", message.id(), operator.name());

        publisher.publish(new SmsEvent(
                SmsEventType.SMS_ROUTED,
                message.id(),
                Instant.now(),
                Map.of("operator", operator.name(), "prefix", operator.prefix())
        ));

        return delivery.deliver(message, operator);
    }
}
