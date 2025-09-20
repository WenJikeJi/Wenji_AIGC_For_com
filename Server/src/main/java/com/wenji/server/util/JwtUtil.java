package com.wenji.server.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    
    // 临时令牌过期时间（30分钟）
    private static final long TEMP_TOKEN_EXPIRATION = 30 * 60 * 1000;
    
    // 从令牌中获取用户名
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    
    // 从令牌中获取用户ID
    public Long getUserIdFromToken(String token) {
        return Long.parseLong(getClaimFromToken(token, claims -> claims.get("userId", String.class)));
    }
    
    // 从令牌中获取角色
    public Integer getUserRoleFromToken(String token) {
        return Integer.parseInt(getClaimFromToken(token, claims -> claims.get("role", String.class)));
    }
    
    // 从令牌中获取过期时间
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    // 从令牌中获取指定的声明
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    // 获取令牌中的所有声明
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }
    
    // 检查令牌是否过期
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    // 生成令牌
    public String generateToken(Long userId, String username, Integer role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("role", role.toString());
        
        return doGenerateToken(claims, username);
    }
    
    // 构建令牌
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }
    
    // 验证令牌
    public Boolean validateToken(String token, String username) {
        final String tokenUsername = getUsernameFromToken(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }
    
    // 生成临时令牌
    public String generateTempToken(Map<String, Object> data) {
        return Jwts.builder()
                .setClaims(data)
                .setSubject("temp_token")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TEMP_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }
    
    // 解析临时令牌
    @SuppressWarnings("unchecked")
    public Map<String, Object> parseTempToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        if (isTokenExpired(token)) {
            throw new RuntimeException("临时令牌已过期");
        }
        return claims;
    }
}