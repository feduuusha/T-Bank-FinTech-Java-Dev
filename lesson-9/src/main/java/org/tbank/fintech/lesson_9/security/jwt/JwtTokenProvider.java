package org.tbank.fintech.lesson_9.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.token.expiryDate.remember}")
    private Duration durationRememberMe;
    @Value("${jwt.token.expiryDate.notRemember}")
    private Duration durationNotRememberMe;
    @Value("${jwt.token.expiryDate.reset}")
    private Duration durationResetToken;
    private ConcurrentHashMap<String, Object> blackList = new ConcurrentHashMap<>();
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public String createToken(Authentication authentication, boolean isRememberMe) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        long expiryMillis = now.getTime();
        if (isRememberMe) {
            expiryMillis += durationRememberMe.toMillis();
        } else {
            expiryMillis += durationNotRememberMe.toMillis();
        }
        Date expiryDate = new Date(expiryMillis);
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String createResetToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + durationResetToken.toMillis());
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String resolveToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }


    public String getUsername(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public void addTokenToBlackList(String token) {
        blackList.put(token, new Object());
    }

    public boolean isTokenBlacklisted(String token) {
        return blackList.containsKey(token);
    }

    @Scheduled(initialDelay = 10, fixedRate = 10, timeUnit = TimeUnit.MINUTES)
    private void removeExpiredTokensFromBlackList() {
        var keysIterator = blackList.keys().asIterator();
        var concurrentMap = new ConcurrentHashMap<String, Object>();
        keysIterator.forEachRemaining((token) -> {
            if (validateToken(token)) {
                concurrentMap.put(token, new Object());
            }
        });
        blackList = concurrentMap;
    }
}