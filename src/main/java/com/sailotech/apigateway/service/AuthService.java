package com.sailotech.apigateway.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("auth-service")
public interface AuthService {

	@GetMapping("auth/token")
	public ResponseEntity<?> validateToken(@RequestHeader("AUTHORIZATION") String jwtToken);
}
