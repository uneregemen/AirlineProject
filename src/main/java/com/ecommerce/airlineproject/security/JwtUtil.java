package com.ecommerce.airlineproject.security; // Kendi paket adını kontrol et

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 32 karakterden uzun çok gizli bir anahtar (Bu anahtarı sadece senin sunucun bilecek!)
    private static final String SECRET_KEY = "MySuperSecretKeyForAirlineProjectMidterm2026";

    // Anahtarı JWT'nin istediği şifreleme formatına çeviriyoruz
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // Token'ın geçerlilik süresi (Örn: 24 saat = 1000 * 60 * 60 * 24 milisaniye)
    private static final long EXPIRATION_TIME = 86400000L;

    // 1. Giriş yapan kullanıcıya Token Üretme
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username) // Biletin sahibi
                .claim("role", role)  // Rolünü de içine ekliyoruz (Admin mi User mı?)
                .setIssuedAt(new Date()) // Veriliş tarihi (Şu an)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Bitiş tarihi
                .signWith(key, SignatureAlgorithm.HS256) // Gizli anahtarımızla imzalıyoruz
                .compact();
    }

    // 2. Gelen Token'ın içinden Kullanıcı Adını Çıkarma
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // 3. Gelen Token'ın içinden Rolü anlama
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // 4. Token'ın Süresi Dolmuş mu veya Sahte mi Diye Kontrol Etme
    public boolean isTokenValid(String token) {
        try {
            return !getClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false; // Token bozuksa, sahteyse veya süresi geçmişse false dön
        }
    }

    // Yardımcı Metot: Token'ın içindeki şifreli verileri (Claims) çözer
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}