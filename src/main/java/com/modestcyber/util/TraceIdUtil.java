package com.modestcyber.util;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * TraceId工具类
 */
public class TraceIdUtil {
    private static final String TRACE_ID = "traceId";

    public static void setTraceId() {
        MDC.put(TRACE_ID, UUID.randomUUID().toString().replace("-", ""));
    }

    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }

    public static void clear() {
        MDC.clear();
    }
}
