package com.sailotech.apigateway.filter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.sailotech.apigateway.service.AuthService;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class JwtTokenGatewayFilterFactory
		extends AbstractGatewayFilterFactory<Object> {
	
	@Autowired
	@Lazy
	private AuthService authService;

	@Override
	public GatewayFilter apply(Object config) {
		return new GatewayFilter() {
			
			@Override
			public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
				var httpRequest = exchange.getRequest();
				List<String> headers = httpRequest.getHeaders().get("AUTHORIZATION");
				if(headers == null || headers.isEmpty() || headers.get(0) == null || headers.get(0).trim().isEmpty()) {
					exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
					return exchange.getResponse().setComplete();
				}
				
				String jwtToken = headers.get(0);
				
				return Mono.fromCallable(() -> {
					try {
						var response = authService.validateToken(jwtToken);
						return response.getStatusCode() == HttpStatus.OK;
					}
					catch (Exception ex) {
						return false;
					}
				}).subscribeOn(Schedulers.boundedElastic())
						.flatMap(isAuthenticated -> {
							if(isAuthenticated)
								return chain.filter(exchange);
							else 
							{
								exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
								return exchange.getResponse().setComplete();
							}
						});
				
			}
		};
	}

//	public static class Config {
//		
//		private String headerName = "AUTHORIZATION";
//		
//		public String getHeaderName() {
//			return headerName;
//		}
//		
//		public void setHeaderName(String headerName) {
//			this.headerName = headerName;
//		}
//
//	}

}
