package com.smsrouter.fixture;

import com.smsrouter.domain.model.SmsEvent;
import com.smsrouter.domain.model.SmsEventType;
import com.smsrouter.port.SmsPublisherPort;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * In-memory fake SMS publisher that captures events for assertion in unit tests.
 */
public class FakeSmsPublisher implements SmsPublisherPort {

    private final List<SmsEvent> published = new ArrayList<>();

    @Override
    public void publish(@NonNull SmsEvent event) {
        published.add(event);
    }

    /** @return unmodifiable view of all published events */
    public List<SmsEvent> getPublished() {
        return Collections.unmodifiableList(published);
    }

    /**
     * @param type event type to look for
     * @return true if at least one event of the given type was published
     */
    public boolean hasEventOfType(@NonNull SmsEventType type) {
        return published.stream().anyMatch(e -> e.eventType() == type);
    }
}
