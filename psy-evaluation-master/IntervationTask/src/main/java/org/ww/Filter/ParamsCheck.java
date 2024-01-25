package org.ww.Filter;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import javax.servlet.*;
import java.io.IOException;
import java.util.logging.LogRecord;


public class ParamsCheck implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
