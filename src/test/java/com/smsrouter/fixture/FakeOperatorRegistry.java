package com.smsrouter.fixture;

import com.smsrouter.domain.exception.OperatorUnavailableException;
import com.smsrouter.domain.exception.UnroutableSmsException;
import com.smsrouter.domain.model.Operator;
import com.smsrouter.port.OperatorRegistryPort;
import org.springframework.lang.NonNull;

import java.util.Comparator;
import java.util.Map;

/**
 * In-memory fake operator registry for unit tests.
 * Uses longest prefix matching, identical to production behaviour.
 */
public class FakeOperatorRegistry implements OperatorRegistryPort {

    private static final Map<String, Operator> PRIMARY = Map.of(
            "+44", new Operator("EE", "+44", "UK", 1),
            "+33", new Operator("Orange FR", "+33", "FR", 1),
            "+49", new Operator("Deutsche Telekom", "+49", "DE", 1),
            "+1",  new Operator("AT&T", "+1", "US", 1),
            "+39", new Operator("TIM", "+39", "IT", 1)
    );

    private static final Map<String, Operator> FALLBACK = Map.of(
            "EE",               new Operator("Vodafone UK", "+44", "UK", 2),
            "Orange FR",        new Operator("SFR", "+33", "FR", 2),
            "Deutsche Telekom", new Operator("Vodafone DE", "+49", "DE", 2),
            "AT&T",             new Operator("T-Mobile US", "+1", "US", 2),
            "TIM",              new Operator("Vodafone IT", "+39", "IT", 2)
    );

    @Override
    public Operator findByPrefix(@NonNull String phoneNumber) {
        return PRIMARY.entrySet().stream()
                .filter(e -> phoneNumber.startsWith(e.getKey()))
                .max(Comparator.comparingInt(e -> e.getKey().length()))
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new UnroutableSmsException(phoneNumber));
    }

    @Override
    public Operator findFallback(@NonNull Operator primary) {
        Operator fallback = FALLBACK.get(primary.name());
        if (fallback == null) {
            throw new OperatorUnavailableException(primary);
        }
        return fallback;
    }
}
