package com.poke.api.util;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TransactionFilter implements Filter {

	// Generates a unique ID per request, adds it to header and MDC for logging
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		String txId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
		response.addHeader("X-Transaction-ID", txId);
		MDC.put("txId", txId);
		try {
			chain.doFilter(req, res);
		} finally {
			MDC.clear();
		}
	}
}