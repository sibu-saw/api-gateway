package com.sailotech.apigateway.filter;

public class JwtTokenGatewayFilterConfig {

	private String headerName = "AUTHORIZATION";
	
	public String getHeaderName() {
		return headerName;
	}
	
	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}
}
