package com.wipo.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.wipo.DTO.JwtDTO;
import com.wipo.DTO.KakaoTokenDTO;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ApiService {

	@Value("${kakao.clientId}")
	private String clientId;
	@Value("${kakao.redirectUrl}")
	private String redirectUrl;
	
	@Value("${naver.clientId}")
	private String naverClientId;
	
	@Value("${naver.secret}")
	private String secret;
	
	private WebClient webClient = WebClient.create();
	
	public Mono<String> tokenApi(String code){
		try {
			String apiUrl = "https://kauth.kakao.com/oauth/token";
			
			return webClient.post().uri(apiUrl)
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.body(BodyInserters.fromFormData("grant_type","authorization_code")
					.with("client_id", clientId)
					.with("redirect_uri",redirectUrl)
					.with("code",code))
					.retrieve()
			        .bodyToMono(String.class);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("ApiService.33 : {}",e);
			return null;
		}
		
	}
	
	public Mono<String> kakaoUserInfo(KakaoTokenDTO dto){
		try {
			String apiUrl = "https://kapi.kakao.com/v2/user/me";
			
			return webClient.post().uri(apiUrl)
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.header("Authorization", "Bearer "+dto.getAccess_token())
					.retrieve()
			        .bodyToMono(String.class);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("ApiService.50 : {}",e);
			return null;
		}
		
	}
	
	public Mono<String> refreshKakaoApi(JwtDTO dto){
		try {
			String apiUrl = "https://kauth.kakao.com/oauth/token";
			
			return webClient.post().uri(apiUrl)
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.body(BodyInserters.fromFormData("grant_type","refresh_token")
					.with("client_id", clientId)
					.with("refresh_token",dto.getRefresh_token()))
					.retrieve()
			        .bodyToMono(String.class);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("ApiService.76 : {}",e);
			return null;
		}
		
	}
	
	public Mono<String> tokenInfo(String code){
		try {
			String apiUrl = "https://oauth2.googleapis.com";
			
			webClient = WebClient.builder().baseUrl(apiUrl).build();
			
			return webClient.get()
					.uri(uriBuilder->uriBuilder.path("/tokeninfo").queryParam("id_token", code).build())
					.retrieve()
					.bodyToMono(String.class);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("ApiService.92 : {}",e);
			return null;
		}
	}
	
	public Mono<String> naverAuthToken(String code){
		try {
			String apiUrl = "https://nid.naver.com/oauth2.0";
			
			webClient = WebClient.builder().baseUrl(apiUrl).build();
			
			return webClient.get().uri(uriBuilder->uriBuilder.path("/token").queryParam("grant_type", "authorization_code")
					.queryParam("client_id", naverClientId)
					.queryParam("client_secret", secret)
					.queryParam("code", code)
					.queryParam("state", UtilService.generateState()).build()).retrieve().bodyToMono(String.class);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("ApiService.118 : {}",e);
			return null;
		}
	}
	
	public Mono<String> naverInfo(String access_token){
		try {
			String apiUrl = "https://openapi.naver.com/v1/nid/me";
			
			return webClient.get().uri(apiUrl).header("Authorization", access_token).retrieve().bodyToMono(String.class);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("ApiService.130 : {}",e);
			return null;
		}
	}
	
}
