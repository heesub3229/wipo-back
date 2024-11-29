package com.wipo.Appconfig;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wipo.DTO.JwtDTO;
import com.wipo.DTO.KakaoTokenDTO;
import com.wipo.Service.ApiService;
import com.wipo.Service.UtilService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtTokenProvider {
	
	@Value("${jwt.secret}")
	private String secretKey;
	
	@Autowired
	private UtilService utilService;
	
	private final ObjectMapper mapper = new ObjectMapper();
		
	public String generateToken(JwtDTO dto) {
		try {
			String dtoJson = mapper.writeValueAsString(dto);
			return Jwts.builder()
					.claim("user", dtoJson)
					.setIssuedAt(new Date())
					.setExpiration(new Date(System.currentTimeMillis()+ dto.getRefresh_token_expires_in()))
					.signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
					.compact();
		}catch (Exception e) {
			// TODO: handle exception
			log.error("JwtTokenProvider.36: {}",e);
			return "";
		}
				
	}
	
	
	public boolean validateJwt(String token) {
		String ret = "";
		try {
			Claims claims = validateToken(token);
			if(claims==null) {
				throw new Exception();
			}
			boolean exfireFlag = claims.getExpiration().after(new Date());
			if(!exfireFlag) {
				throw new Exception();
			}
			
			return false;
		}catch (Exception e) {
			// TODO: handle exception
			return true;
		}
	}
	
	public Claims validateToken(String token) {
		try {
			return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
		}catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	
}
