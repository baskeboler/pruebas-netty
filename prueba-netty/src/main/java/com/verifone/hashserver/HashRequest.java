package com.verifone.hashserver;

import io.netty.handler.codec.http.HttpRequest;

public class HashRequest {
	private HttpRequest httpRequest;
	private String data;

	public HttpRequest getHttpRequest() {
		return httpRequest;
	}

	public void setHttpRequest(HttpRequest httpRequest) {
		this.httpRequest = httpRequest;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
