package com.jztchl.splitwiseclonejava.filter;

import com.jztchl.splitwiseclonejava.repos.UserRepository;
import com.jztchl.splitwiseclonejava.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                String email = jwtService.extractEmail(token);
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    userRepo.findByEmail(email).ifPresent(user -> {
                        if (jwtService.validateToken(token, email)) {
                            UsernamePasswordAuthenticationToken auth =
                                    new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    });
                }
            }
        } catch (Exception ignored) {
            // On any JWT parsing/validation error, proceed without setting authentication
        }
        filterChain.doFilter(request, response);
    }
}

