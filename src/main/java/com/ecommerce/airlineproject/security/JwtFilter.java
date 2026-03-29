package com.ecommerce.airlineproject.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Müşterinin isteğinden "Authorization" (Yetki) başlığını alıyoruz
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;
        String role = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // "Bearer " kısmını (ilk 7 harfi) çöpe at, sadece şifreli token'ı al
            try {
                username = jwtUtil.extractUsername(jwt);
                role = jwtUtil.extractRole(jwt);
            } catch (Exception e) {
                System.out.println("Geçersiz veya süresi dolmuş Token: " + e.getMessage());
            }
        }

        // 3. Eğer token'dan bir isim çıktıysa ve şu anki sistemde kimse kayıtlı görünmüyorsa:
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 4. JwtUtil'e sor: Bu token hala geçerli mi?
            if (jwtUtil.isTokenValid(jwt)) {

                // 5. Geçerliyse Spring Security'ye "Bu adam temiz, içeri al ve rolünü kaydet" diyoruz
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username, null, Collections.singletonList(new SimpleGrantedAuthority(role)));

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. Kişiyi güvenli bölgeye (Security Context) yerleştir
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 7. Güvenlik kontrolü bitti, isteğin hedefine (örneğin Controller'a) gitmesine izin ver
        filterChain.doFilter(request, response);
    }
}