package com.smsrouter.domain.service;

import com.smsrouter.domain.model.Operator;
import com.smsrouter.port.OperatorRegistryPort;
import org.springframework.lang.NonNull;

/**
 * Resolves the target operator from an E.164 phone number.
 * Delegates prefix lookup to {@link OperatorRegistryPort}.
 */
public class PrefixResolver {

    private final OperatorRegistryPort registry;

    /**
     * @param registry operator registry to delegate prefix lookup to
     */
    public PrefixResolver(@NonNull OperatorRegistryPort registry) {
        this.registry = registry;
    }

    /**
     * Resolve the operator responsible for the given E.164 phone number.
     *
     * @param phoneNumber E.164 formatted phone number (must start with '+')
     * @return the matching {@link Operator}
     * @throws IllegalArgumentException if phoneNumber is null or not E.164
     * @throws com.smsrouter.domain.exception.UnroutableSmsException if no operator matches
     */
    public Operator resolve(@NonNull String phoneNumber) {
        if (phoneNumber == null || !phoneNumber.startsWith("+")) {
            throw new IllegalArgumentException(
                    "Phone number must be in E.164 format (starting with '+'): " + phoneNumber);
        }
        return registry.findByPrefix(phoneNumber);
    }
}
