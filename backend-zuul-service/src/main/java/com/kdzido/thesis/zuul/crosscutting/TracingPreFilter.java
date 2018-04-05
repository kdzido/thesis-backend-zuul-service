package com.kdzido.thesis.zuul.crosscutting;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Enriches incoming HTTP request with a unique correlation ID added as a header.
 */
@Component
public class TracingPreFilter extends ZuulFilter {

    private static final Logger logger = LoggerFactory.getLogger(TracingPreFilter.class);

    private static final int FILTER_ORDER = 1;
    private static final boolean FILTER_ENABLED = true;

    @Autowired
    FilterUtil filterUtil;

    @Override
    public String filterType() {
        return FilterUtil.PRE_FILTER_TYPE;
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
        // TODO rename/refactor

        if (filterUtil.isCorrelationIdHeaderPresent()) {
            logger.debug("{} found in tracing pre-filter: {} ",
                    FilterUtil.HEADER_CORRELATION_ID,
                    filterUtil.getCorrelationId());
        } else {
            filterUtil.setCorrelationId(filterUtil.generateCorrelationId());
            logger.debug("{} generated in tracing pre-filter: {} ",
                    FilterUtil.HEADER_CORRELATION_ID,
                    filterUtil.getCorrelationId());
        }

        final RequestContext ctx = RequestContext.getCurrentContext();

        logger.debug(String.format("Processing incoming request for {}.",  ctx.getRequest().getRequestURI()));

        return null;
    }
}
