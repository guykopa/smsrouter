package com.smsrouter.port;

import com.smsrouter.domain.model.Operator;
import com.smsrouter.domain.model.RoutingResult;
import com.smsrouter.domain.model.SmsMessage;
import org.springframework.lang.NonNull;

/**
 * Outbound port for delivering an SMS message via a given operator.
 */
public interface SmsDeliveryPort {

    /**
     * Attempt to deliver the given SMS message via the specified operator.
     *
     * @param message  the SMS message to deliver
     * @param operator the target operator
     * @return {@link RoutingResult} describing outcome and latency
     */
    RoutingResult deliver(@NonNull SmsMessage message, @NonNull Operator operator);
}
