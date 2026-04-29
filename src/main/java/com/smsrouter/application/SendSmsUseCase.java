package com.smsrouter.application;

import com.smsrouter.domain.exception.UnroutableSmsException;
import com.smsrouter.domain.model.RoutingResult;
import com.smsrouter.domain.model.SmsEvent;
import com.smsrouter.domain.model.SmsEventType;
import com.smsrouter.domain.model.SmsMessage;
import com.smsrouter.domain.model.SmsStatus;
import com.smsrouter.domain.service.SmsRoutingService;
import com.smsrouter.port.SmsPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.util.Map;

/**
 * Use case: receive an SMS and orchestrate its routing and delivery.
 *
 * <p>Publishes SMS_RECEIVED on entry, delegates routing to
 * {@link SmsRoutingService}, then publishes SMS_DELIVERED or SMS_FAILED.
 * No Spring annotations — wired by the Spring context via constructor injection.
 */
public class SendSmsUseCase {

    private static final Logger log = LoggerFactory.getLogger(SendSmsUseCase.class);

    private final SmsRoutingService routingService;
    private final SmsPublisherPort publisher;

    /**
     * @param routingService domain routing service
     * @param publisher      domain event publisher port
     */
    public SendSmsUseCase(
            @NonNull SmsRoutingService routingService,
            @NonNull SmsPublisherPort publisher) {
        this.routingService = routingService;
        this.publisher = publisher;
    }

    /**
     * Send an SMS through the routing engine.
     *
     * <p>Event sequence: SMS_RECEIVED → SMS_ROUTED → SMS_DELIVERED | SMS_FAILED.
     * Unroutable numbers publish SMS_FAILED to the DLQ before re-throwing.
     *
     * @param message the incoming SMS message
     * @return {@link RoutingResult} with final status and latency
     * @throws UnroutableSmsException if prefix has no operator
     */
    public RoutingResult send(@NonNull SmsMessage message) {
        log.info("Received SMS {} from {} to {}", message.id(), message.from(), message.to());

        publisher.publish(new SmsEvent(
                SmsEventType.SMS_RECEIVED,
                message.id(),
                Instant.now(),
                Map.of("from", message.from(), "to", message.to())
        ));

        RoutingResult result;
        try {
            result = routingService.route(message);
        } catch (UnroutableSmsException e) {
            publisher.publish(new SmsEvent(
                    SmsEventType.SMS_FAILED,
                    message.id(),
                    Instant.now(),
                    Map.of("reason", "UNROUTABLE", "phoneNumber", message.to())
            ));
            throw e;
        }

        SmsEventType outcome = result.status() == SmsStatus.DELIVERED
                ? SmsEventType.SMS_DELIVERED
                : SmsEventType.SMS_FAILED;

        publisher.publish(new SmsEvent(
                outcome,
                message.id(),
                Instant.now(),
                Map.of(
                        "operator", result.operator().name(),
                        "status",   result.status().name(),
                        "latencyMs", result.latencyMs()
                )
        ));

        return result;
    }
}
