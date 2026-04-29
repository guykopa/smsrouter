package com.smsrouter.application;

import com.smsrouter.adapter.delivery.SimulatedSmsDelivery;
import com.smsrouter.adapter.registry.InMemoryOperatorRegistry;
import com.smsrouter.domain.service.SmsRoutingService;
import com.smsrouter.port.SmsPublisherPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring wiring for domain services and use cases.
 * Only this class knows about concrete adapter implementations (DIP).
 */
@Configuration
public class SmsRouterConfig {

    /**
     * @param registry  operator registry adapter
     * @param delivery  delivery simulation adapter
     * @param publisher Kafka event publisher port
     * @return configured domain routing service
     */
    @Bean
    public SmsRoutingService smsRoutingService(
            InMemoryOperatorRegistry registry,
            SimulatedSmsDelivery delivery,
            SmsPublisherPort publisher) {
        return new SmsRoutingService(registry, delivery, publisher);
    }

    /**
     * @param routingService domain routing service
     * @param publisher      event publisher port
     * @return configured send SMS use case
     */
    @Bean
    public SendSmsUseCase sendSmsUseCase(
            SmsRoutingService routingService,
            SmsPublisherPort publisher) {
        return new SendSmsUseCase(routingService, publisher);
    }
}
