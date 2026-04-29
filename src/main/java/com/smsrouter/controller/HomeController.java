package com.smsrouter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Informational endpoint listing available API routes.
 */
@RestController
@RequestMapping("/")
public class HomeController {

    /**
     * @return 200 OK with the list of available endpoints
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> index() {
        return ResponseEntity.ok(Map.of(
                "application", "smsrouter",
                "endpoints", List.of(
                        Map.of("method", "POST", "path", "/api/sms/send",    "description", "Submit an SMS for routing"),
                        Map.of("method", "GET",  "path", "/actuator/health", "description", "Application health"),
                        Map.of("method", "GET",  "path", "/actuator/metrics","description", "Application metrics")
                )
        ));
    }
}
