package com.kdzido.thesis.zuul.crosscutting;

import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FilterUtil {

    static final String PRE_FILTER_TYPE = "pre";
    static final String POST_FILTER_TYPE = "post";
    static final String ROUTE_FILTER_TYPE = "route";

    static final String HEADER_CORRELATION_ID = "thesis-correlation-id";

    /**
     * @return the correlation ID
     */
    public String getCorrelationId() {
        final RequestContext ctx = RequestContext.getCurrentContext();
        if (ctx.getRequest().getHeader(HEADER_CORRELATION_ID) != null) {
            return ctx.getRequest().getHeader(HEADER_CORRELATION_ID);
        } else {
            return ctx.getZuulRequestHeaders().get(HEADER_CORRELATION_ID);
        }
    }

    /**
     * @return true if the correlation ID header present
     */
    public boolean isCorrelationIdHeaderPresent() {
        return getCorrelationId() != null;
    }

    /**
     * @param correlationId the correlation ID
     */
    public void setCorrelationId(final String correlationId) {
        final RequestContext ctx = RequestContext.getCurrentContext();
        ctx.addZuulRequestHeader(HEADER_CORRELATION_ID, correlationId);
    }

    /**
     * @return the generated correlation ID
     */
    public String generateCorrelationId(){
        return UUID.randomUUID().toString();
    }

}
