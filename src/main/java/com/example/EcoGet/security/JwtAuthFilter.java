package com.example.EcoGet.security;

import com.example.EcoGet.service.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private JwtService jwtService;
    private UsuarioService usuarioService;

    public JwtAuthFilter(JwtService jwtService, UsuarioService usuarioService) {
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain) throws ServletException, IOException {

        String authorization = httpServletRequest.getHeader("Authorization");

        log.debug("JwtAuthFilter -> {} {} | Authorization: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                authorization != null ? authorization.substring(0, Math.min(30, authorization.length())) + "..." : "null");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            boolean isValid = jwtService.tokenValido(token);

            log.debug("Token válido: {}", isValid);

            if (isValid) {
                String emailUsuario = jwtService.obterEmailUsuario(token);
                UserDetails usuario = usuarioService.loadUserByUsername(emailUsuario);
                UsernamePasswordAuthenticationToken user = new
                        UsernamePasswordAuthenticationToken(usuario, null,
                        usuario.getAuthorities());
                user.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(user);
                log.debug("Autenticado: {} | Roles: {}", emailUsuario, usuario.getAuthorities());
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
