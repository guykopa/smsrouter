package com.smsrouter.port;

import com.smsrouter.domain.model.SmsEvent;
import org.springframework.lang.NonNull;

/**
 * Outbound port for publishing domain events to the message broker.
 */
public interface SmsPublisherPort {

    /**
     * Publish a domain event to the appropriate topic.
     *
     * @param event the domain event to publish
     */
    void publish(@NonNull SmsEvent event);
}
