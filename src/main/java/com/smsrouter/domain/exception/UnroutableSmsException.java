package com.smsrouter.domain.exception;

import org.springframework.lang.NonNull;

/**
 * Thrown when no operator can be found for a given phone number prefix.
 */
public class UnroutableSmsException extends RuntimeException {

    private final String phoneNumber;

    /**
     * @param phoneNumber the E.164 number that could not be routed
     */
    public UnroutableSmsException(@NonNull String phoneNumber) {
        super("No operator found for phone number: " + phoneNumber);
        this.phoneNumber = phoneNumber;
    }

    /** @return the unroutable phone number */
    public String getPhoneNumber() {
        return phoneNumber;
    }
}
