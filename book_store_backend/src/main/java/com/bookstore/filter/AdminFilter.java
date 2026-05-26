package com.bookstore.filter;

import com.bookstore.util.JsonUtil;
import com.bookstore.util.SessionUtil;
import jakarta.json.Json;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class AdminFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        if (session == null || !SessionUtil.isAdminLoggedIn(session)) {
            JsonUtil.writeJson(httpResponse, HttpServletResponse.SC_UNAUTHORIZED,
                Json.createObjectBuilder().add("error", "Unauthorized").build());
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
