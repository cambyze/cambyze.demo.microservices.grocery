package com.cambyze.demo.microservices.grocery.gatewayserver.filters;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class LogCallsFilter extends ZuulFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogCallsFilter.class);
  private static final String FILTER_PRE = "pre";

  @Override
  public boolean shouldFilter() {
    return true;
  }

  @Override
  public Object run() throws ZuulException {
    HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
    String method = request.getMethod();
    String uri = request.getRequestURI();
    String host = request.getHeader("host");
    String userAgent = request.getHeader("User-Agent");
    LOGGER.info("Request: " + method + " " + uri + " from " + host + " : " + userAgent);
    return null;
  }

  @Override
  public String filterType() {
    return FILTER_PRE;
  }

  @Override
  public int filterOrder() {
    return 1;
  }

}
