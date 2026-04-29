package com.smsrouter.unit;

import com.smsrouter.domain.exception.UnroutableSmsException;
import com.smsrouter.domain.model.RoutingResult;
import com.smsrouter.domain.model.SmsEventType;
import com.smsrouter.domain.model.SmsStatus;
import com.smsrouter.domain.service.SmsRoutingService;
import com.smsrouter.fixture.FakeOperatorRegistry;
import com.smsrouter.fixture.FakeSmsDelivery;
import com.smsrouter.fixture.FakeSmsPublisher;
import com.smsrouter.fixture.SmsFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SmsRoutingService}.
 * No Spring context, no Kafka — all dependencies replaced by fakes.
 */
class SmsRoutingServiceTest {

    private SmsRoutingService service;
    private FakeSmsPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new FakeSmsPublisher();
        service = new SmsRoutingService(
                new FakeOperatorRegistry(),
                new FakeSmsDelivery(),
                publisher
        );
    }

    @Test
    void routesNormalSmsToCorrectOperator() {
        RoutingResult result = service.route(SmsFixtures.normalSms());
        assertEquals("EE", result.operator().name());
    }

    @Test
    void returnsRoutingResultWithDeliveredStatus() {
        RoutingResult result = service.route(SmsFixtures.normalSms());
        assertEquals(SmsStatus.DELIVERED, result.status());
    }

    @Test
    void throwsUnroutableExceptionForUnknownPrefix() {
        assertThrows(UnroutableSmsException.class,
                () -> service.route(SmsFixtures.unroutableSms()));
    }

    @Test
    void publishesSmsRoutedEventAfterRouting() {
        service.route(SmsFixtures.normalSms());
        assertTrue(publisher.hasEventOfType(SmsEventType.SMS_ROUTED));
    }

    @Test
    void measuresDeliveryLatency() {
        RoutingResult result = service.route(SmsFixtures.normalSms());
        assertTrue(result.latencyMs() >= 0);
    }
}
