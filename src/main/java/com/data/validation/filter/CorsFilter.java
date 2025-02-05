package com.data.validation.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

// spotless:off
@WebFilter(displayName = "CorsFilter", urlPatterns = {"/*"})
public class CorsFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS,  DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Authorization,Content-Type, x-requested-with");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setCharacterEncoding("UTF-8");

        chain.doFilter(req, servletResponse);
    }
}
// spotless:on
