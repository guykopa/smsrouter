package com.smsrouter.unit;

import com.smsrouter.application.SendSmsUseCase;
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
 * Unit tests for {@link SendSmsUseCase}.
 * No Spring context, no Kafka — all dependencies replaced by fakes.
 */
class SendSmsUseCaseTest {

    private SendSmsUseCase useCase;
    private FakeSmsPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new FakeSmsPublisher();
        SmsRoutingService routingService = new SmsRoutingService(
                new FakeOperatorRegistry(),
                new FakeSmsDelivery(),
                publisher
        );
        useCase = new SendSmsUseCase(routingService, publisher);
    }

    @Test
    void sendSmsPublishesSmsReceivedEvent() {
        useCase.send(SmsFixtures.normalSms());
        assertTrue(publisher.hasEventOfType(SmsEventType.SMS_RECEIVED));
    }

    @Test
    void sendSmsReturnsRoutingResult() {
        RoutingResult result = useCase.send(SmsFixtures.normalSms());
        assertNotNull(result);
        assertEquals(SmsStatus.DELIVERED, result.status());
    }

    @Test
    void sendSmsToUnknownPrefixThrows() {
        assertThrows(UnroutableSmsException.class,
                () -> useCase.send(SmsFixtures.unroutableSms()));
    }

    @Test
    void sendSmsToUnknownPrefixPublishesFailedEventToDlq() {
        assertThrows(UnroutableSmsException.class,
                () -> useCase.send(SmsFixtures.unroutableSms()));
        assertTrue(publisher.hasEventOfType(SmsEventType.SMS_FAILED));
    }

    @Test
    void sendSmsPublishesDeliveredEventOnSuccess() {
        useCase.send(SmsFixtures.normalSms());
        assertTrue(publisher.hasEventOfType(SmsEventType.SMS_DELIVERED));
    }
}
