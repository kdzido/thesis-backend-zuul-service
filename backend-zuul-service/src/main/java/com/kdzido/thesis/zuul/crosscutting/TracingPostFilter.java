package com.kdzido.thesis.zuul.crosscutting;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Enriches HTTP response with the correlation ID from the HTTP request header.
 */
@Component
public class TracingPostFilter extends ZuulFilter {

    private static final Logger logger = LoggerFactory.getLogger(TracingPostFilter.class);

    private static final int FILTER_ORDER = 1;
    private static final boolean FILTER_ENABLED = true;

    @Autowired
    FilterUtil filterUtil;

    @Override
    public String filterType() {
        return FilterUtil.POST_FILTER_TYPE;
    }

    @Override
    public int filterOrder() {
        return FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return FILTER_ENABLED;
    }

    @Override
    public Object run() {
        final RequestContext ctx = RequestContext.getCurrentContext();
        ctx.getResponse().addHeader(FilterUtil.HEADER_CORRELATION_ID, filterUtil.getCorrelationId());

        return null;
    }
}
