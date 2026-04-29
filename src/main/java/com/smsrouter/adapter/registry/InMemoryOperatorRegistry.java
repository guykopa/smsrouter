package com.smsrouter.adapter.registry;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smsrouter.domain.exception.OperatorUnavailableException;
import com.smsrouter.domain.exception.UnroutableSmsException;
import com.smsrouter.domain.model.Operator;
import com.smsrouter.port.OperatorRegistryPort;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Operator registry backed by {@code routing-table.json} loaded at startup.
 * Implements longest prefix matching for E.164 number resolution.
 */
@Component
public class InMemoryOperatorRegistry implements OperatorRegistryPort {

    private static final Logger log = LoggerFactory.getLogger(InMemoryOperatorRegistry.class);

    private final Map<String, Operator> primaryTable  = new HashMap<>();
    private final Map<String, Operator> fallbackTable = new HashMap<>();

    /** Loads routing-table.json from the classpath at application startup. */
    @PostConstruct
    void loadRoutingTable() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> entries = mapper.readValue(
                new ClassPathResource("routing-table.json").getInputStream(),
                new TypeReference<>() {}
        );

        for (Map<String, Object> entry : entries) {
            String prefix  = (String) entry.get("prefix");
            String country = (String) entry.get("country");

            Operator primary = new Operator((String) entry.get("primaryOperator"), prefix, country, 1);
            primaryTable.put(prefix, primary);

            if (entry.containsKey("fallbackOperator")) {
                Operator fallback = new Operator((String) entry.get("fallbackOperator"), prefix, country, 2);
                fallbackTable.put(primary.name(), fallback);
            }
        }
        log.info("Loaded {} operators from routing-table.json", primaryTable.size());
    }

    @Override
    public Operator findByPrefix(@NonNull String phoneNumber) {
        return primaryTable.entrySet().stream()
                .filter(e -> phoneNumber.startsWith(e.getKey()))
                .max(Comparator.comparingInt(e -> e.getKey().length()))
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new UnroutableSmsException(phoneNumber));
    }

    @Override
    public Operator findFallback(@NonNull Operator primary) {
        return Optional.ofNullable(fallbackTable.get(primary.name()))
                .orElseThrow(() -> new OperatorUnavailableException(primary));
    }
}
