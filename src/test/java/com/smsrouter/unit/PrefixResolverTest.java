package com.smsrouter.unit;

import com.smsrouter.domain.exception.UnroutableSmsException;
import com.smsrouter.domain.model.Operator;
import com.smsrouter.domain.service.PrefixResolver;
import com.smsrouter.fixture.FakeOperatorRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PrefixResolver}.
 * No Spring context, no Kafka — pure domain logic.
 */
class PrefixResolverTest {

    private PrefixResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new PrefixResolver(new FakeOperatorRegistry());
    }

    @Test
    void resolvesUkNumber() {
        Operator result = resolver.resolve("+447911123456");
        assertEquals("EE", result.name());
    }

    @Test
    void resolvesFrenchNumber() {
        Operator result = resolver.resolve("+33612345678");
        assertEquals("Orange FR", result.name());
    }

    @Test
    void throwsForUnknownPrefix() {
        assertThrows(UnroutableSmsException.class,
                () -> resolver.resolve("+99999999999"));
    }

    @Test
    void usesLongestPrefixMatch() {
        Operator result = resolver.resolve("+14165551234");
        assertEquals("AT&T", result.name());
    }

    @Test
    void throwsForNullNumber() {
        assertThrows(IllegalArgumentException.class,
                () -> resolver.resolve(null));
    }

    @Test
    void throwsForInvalidE164Format() {
        assertThrows(IllegalArgumentException.class,
                () -> resolver.resolve("0612345678"));
    }
}
