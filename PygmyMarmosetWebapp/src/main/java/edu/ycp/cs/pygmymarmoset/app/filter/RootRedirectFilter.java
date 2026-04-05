package edu.ycp.cs.pygmymarmoset.app.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import edu.ycp.cs.pygmymarmoset.app.model.User;

public class RootRedirectFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req_, ServletResponse resp_, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) req_;
        HttpServletResponse resp = (HttpServletResponse) resp_;

        String pathInfo = req.getServletPath();
        if ("/".equals(pathInfo) || "".equals(pathInfo)) {
            HttpSession session = req.getSession(false);
            User user = session != null ? (User) session.getAttribute("user") : null;
            if (user == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
            } else {
                resp.sendRedirect(req.getContextPath() + "/index");
            }
            return;
        }
        chain.doFilter(req_, resp_);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {}

    @Override
    public void destroy() {}
}