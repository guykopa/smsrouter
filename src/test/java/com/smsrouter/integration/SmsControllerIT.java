package com.smsrouter.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for POST /api/sms/send.
 * Uses embedded Kafka — no external broker required.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(
        partitions = 1,
        topics = {"sms.inbound", "sms.events", "sms.dlq"}
)
@ActiveProfiles("test")
class SmsControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void sendSmsReturnsAccepted() {
        var request = Map.of(
                "from", "+33612345678",
                "to",   "+447911123456",
                "text", "Integration test"
        );

        var response = restTemplate.postForEntity("/api/sms/send", request, Map.class);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().get("id"));
    }

    @Test
    void rootReturnsAvailableRoutes() {
        var response = restTemplate.getForEntity("/", Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().get("endpoints"));
    }

    @Test
    void sendSmsToUnknownPrefixReturnsUnprocessableEntity() {
        var request = Map.of(
                "from", "+33612345678",
                "to",   "+99999999999",
                "text", "Test"
        );

        var response = restTemplate.postForEntity("/api/sms/send", request, Map.class);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    }
}
