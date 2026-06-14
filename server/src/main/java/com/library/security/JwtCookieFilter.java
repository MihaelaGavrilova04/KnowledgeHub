package com.library.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

public class JwtCookieFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtCookieFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Arrays.stream(cookies)
                    .filter(c -> "AUTH_TOKEN".equals(c.getName()))
                    .findFirst()
                    .ifPresent(cookie -> {
                        jwtUtil.getEmailIfValid(cookie.getValue())
                                .ifPresent(email -> {
                                    UserDetails user = userDetailsService.loadUserByUsername(email);
                                    UsernamePasswordAuthenticationToken auth =
                                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                                    SecurityContextHolder.getContext().setAuthentication(auth);
                                });
                    });
        }
        chain.doFilter(request, response);
    }
}