package com.smsrouter.adapter.kafka;

import com.smsrouter.domain.model.SmsEvent;
import com.smsrouter.domain.model.SmsEventType;
import com.smsrouter.port.SmsPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Kafka implementation of {@link SmsPublisherPort}.
 *
 * <p>Topic routing:
 * <ul>
 *   <li>SMS_RECEIVED  → sms.inbound</li>
 *   <li>SMS_FAILED    → sms.dlq</li>
 *   <li>all others    → sms.events</li>
 * </ul>
 */
@Component
public class KafkaSmsPublisher implements SmsPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaSmsPublisher.class);

    static final String TOPIC_INBOUND = "sms.inbound";
    static final String TOPIC_EVENTS  = "sms.events";
    static final String TOPIC_DLQ     = "sms.dlq";

    private final KafkaTemplate<String, SmsEvent> kafkaTemplate;

    /**
     * @param kafkaTemplate Spring Kafka template for {@link SmsEvent} serialisation
     */
    public KafkaSmsPublisher(@NonNull KafkaTemplate<String, SmsEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(@NonNull SmsEvent event) {
        String topic = resolveTopic(event.eventType());
        log.debug("Publishing {} to {}", event.eventType(), topic);
        kafkaTemplate.send(topic, event.smsId().toString(), event);
    }

    private String resolveTopic(@NonNull SmsEventType type) {
        return switch (type) {
            case SMS_RECEIVED -> TOPIC_INBOUND;
            case SMS_FAILED   -> TOPIC_DLQ;
            default           -> TOPIC_EVENTS;
        };
    }
}
