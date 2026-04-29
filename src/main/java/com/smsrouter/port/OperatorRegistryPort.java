package com.smsrouter.port;

import com.smsrouter.domain.exception.OperatorUnavailableException;
import com.smsrouter.domain.exception.UnroutableSmsException;
import com.smsrouter.domain.model.Operator;
import org.springframework.lang.NonNull;

/**
 * Outbound port for resolving mobile network operators from phone prefixes.
 */
public interface OperatorRegistryPort {

    /**
     * Find the primary operator for the given E.164 phone number using
     * longest prefix matching.
     *
     * @param phoneNumber E.164 formatted phone number (e.g. "+447911123456")
     * @return matching primary {@link Operator}
     * @throws UnroutableSmsException if no operator matches the prefix
     */
    Operator findByPrefix(@NonNull String phoneNumber);

    /**
     * Find the fallback operator configured for a given primary operator.
     *
     * @param primary the primary operator that failed delivery
     * @return the fallback {@link Operator}
     * @throws OperatorUnavailableException if no fallback is configured
     */
    Operator findFallback(@NonNull Operator primary);
}
