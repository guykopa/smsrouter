package com.smsrouter.domain.exception;

import com.smsrouter.domain.model.Operator;
import org.springframework.lang.NonNull;

/**
 * Thrown when an operator is unavailable and no fallback can be found.
 */
public class OperatorUnavailableException extends RuntimeException {

    private final Operator operator;

    /**
     * @param operator the operator that is unavailable
     */
    public OperatorUnavailableException(@NonNull Operator operator) {
        super("Operator unavailable: " + operator.name());
        this.operator = operator;
    }

    /** @return the unavailable operator */
    public Operator getOperator() {
        return operator;
    }
}
