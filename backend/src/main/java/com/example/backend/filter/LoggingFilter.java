package com.example.backend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);
    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_MDC_KEY = "X-Trace-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        String traceId = request.getHeader(TRACE_ID_HEADER);

        // Устанавливаем Trace ID в MDC (Mapped Diagnostic Context),
        // чтобы он был доступен в паттерне лога (%X{X-Trace-Id})
        MDC.put(TRACE_ID_MDC_KEY, traceId);

        responseWrapper.setHeader(TRACE_ID_HEADER, traceId);

        long startTime = System.currentTimeMillis();

        try {
            logRequest(requestWrapper, traceId);

            filterChain.doFilter(requestWrapper, responseWrapper);

        } finally {
            responseWrapper.copyBodyToResponse();

            MDC.remove(TRACE_ID_MDC_KEY);
        }
    }

    private void logRequest(ContentCachingRequestWrapper request, String traceId) {
        StringBuilder msg = new StringBuilder();
        msg.append(request.getMethod()).append(" ");
        msg.append(request.getRequestURL());

        String queryString = request.getQueryString();
        if (queryString != null) {
            msg.append('?').append(queryString);
        }

        log.info(msg.toString());
    }

}