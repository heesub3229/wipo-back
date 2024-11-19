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
	
	@Autowired
	private ApiService apiService;
	
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
	
	
	public String validateJwt(String token) {
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
			
			JwtDTO jwtToken = validateAccess(claims);
			
			if(jwtToken == null) {
				throw new Exception();
			}
			String json = claims.get("user",String.class);
			JwtDTO jwtDto = utilService.parseJsonToDto(json, JwtDTO.class);
			
			if(jwtDto.getAccess_token().equals(jwtToken.getAccess_token())) {
				
			}else {
				ret = generateToken(jwtToken);
			}
			return ret;
		}catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	
	private Claims validateToken(String token) {
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
	
	private JwtDTO validateAccess(Claims claims) {
		
		JwtDTO token = null;
		KakaoTokenDTO kakaoToken = null;
		try {
			String json = claims.get("user",String.class);
			
			JwtDTO jwtDto = utilService.parseJsonToDto(json, JwtDTO.class);
			
			Date jwtDt = claims.getIssuedAt();
			
			Date convJwtDt = new Date(jwtDt.getTime()+jwtDto.getExpires_in()*1000);
			
			boolean accessFlag = convJwtDt.after(new Date());
			
			if(accessFlag) {
				return jwtDto;
			}else {
				Mono<String> apiRet = apiService.refreshKakaoApi(jwtDto);
				if(apiRet==null) {
					throw new Exception();
				}
				kakaoToken = utilService.parseJsonToDto(apiRet.block(), KakaoTokenDTO.class);
			}
			
			if(kakaoToken==null) {
				throw new Exception();
			}
			
			token.setSid(jwtDto.getSid());
			token.setAccess_token(kakaoToken.getAccess_token());
			token.setExpires_in(kakaoToken.getExpires_in());
			token.setRefresh_token(kakaoToken.getRefresh_token());
			token.setRefresh_token_expires_in(kakaoToken.getRefresh_token_expires_in());
			token.setType(jwtDto.getType());
			
			return token;
		}catch (Exception e) {
			// TODO: handle exception
			return null;
		}
		
		
	}
}
