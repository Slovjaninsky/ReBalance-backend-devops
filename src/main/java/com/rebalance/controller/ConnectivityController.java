package com.rebalance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Connectivity test")
@RestController
@RequestMapping(APIVersion.current)
public class ConnectivityController {
    @Operation(summary = "Test connectivity")
    @GetMapping("/connect/test")
    public ResponseEntity<String> testConnection() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
