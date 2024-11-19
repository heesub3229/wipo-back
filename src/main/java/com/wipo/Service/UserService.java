package com.wipo.Service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wipo.Appconfig.JwtTokenProvider;
import com.wipo.DTO.GoogleResDTO;
import com.wipo.DTO.JwtDTO;
import com.wipo.DTO.KakaoTokenDTO;
import com.wipo.DTO.KakaoUserDTO;
import com.wipo.DTO.NaverInfoDTO;
import com.wipo.DTO.NaverTokenDTO;
import com.wipo.DTO.ResponseDTO;
import com.wipo.Entity.UserEntity;
import com.wipo.Repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ApiService apiService;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	public ResponseDTO<String> kakaoLogin(String code){
		
		try {
			Mono<String> tokenRes = apiService.tokenApi(code);
			
			if(tokenRes==null) {
				throw new Exception("카카오로그인 에러");
			}
			
			KakaoTokenDTO tokenDto = UtilService.parseJsonToDto(tokenRes.block(), KakaoTokenDTO.class);
			
			if(tokenDto==null) {
				throw new Exception("카카오로그인 에러");
			}
			
			Mono<String> userInfo = apiService.kakaoUserInfo(tokenDto);
			
			if(userInfo==null) {
				throw new Exception("카카오정보조회 에러");
			}
			
			KakaoUserDTO kakaoUsrInfo = UtilService.parseJsonToDto(userInfo.block(), KakaoUserDTO.class);
			
			if(kakaoUsrInfo==null) {
				throw new Exception("카카오정보조회 에러");
			}
			
			UserEntity userEntities = snsLogin("K",
												kakaoUsrInfo.getKakao_account().getEmail(),
												tokenDto.getAccess_token(),
												kakaoUsrInfo.getKakao_account().getProfile().getNickname());
			
			if(userEntities == null) {
				throw new Exception("로그인에러");
			}
			
			JwtDTO jwtDto = JwtDTO.builder()
								.access_token(tokenDto.getAccess_token())
								.expires_in(tokenDto.getExpires_in())
								.id_token(tokenDto.getId_token())
								.refresh_token(tokenDto.getRefresh_token())
								.refresh_token_expires_in(tokenDto.getRefresh_token_expires_in())
								.sid(userEntities.getSid())
								.type(userEntities.getLogin_type())
								.build();
			
			String jwtToken = jwtTokenProvider.generateToken(jwtDto);
			
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
										.errFlag(false)
										.resDate(new Date())
										.data(jwtToken)
										.build();
			
			return ret;
		}catch (Exception e) {
			// TODO: handle exception
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(new Date())
					.data(e.getMessage())
					.build();
			
			log.error("UserService.92 : {}",e);
			
			return ret;
		}
		
		
		
		
		
		
	}
	
	private UserEntity snsLogin(String login_type,
								String email,
								String access_token,
								String name) {
		try {
			UserEntity userEntities = userRepository.findByEmail(email);
			
			if(userEntities != null) {
				userEntities.setPassword(access_token);
				
			}else {
				userEntities = UserEntity.builder()
								.create_at(new Date())
								.email(email)
								.isPrivacy(true)
								.login_type(login_type) // type kakao:K google:G Naver:N
								.name(name)
								.password(access_token)
								.build();
				
			}
			
			return userRepository.save(userEntities);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserService.125 : {}",e);
			return null;
			
		}
	}
	
	public ResponseDTO<String> googleLogin(String credential){
		try {
			Mono<String> apiData = apiService.tokenInfo(credential);
			if(apiData==null) {
				throw new Exception("구글로그인 에러");
			}
			GoogleResDTO tokenDto = UtilService.parseJsonToDto(apiData.block(), GoogleResDTO.class);
			if(tokenDto==null) {
				throw new Exception("구글로그인 에러");
			}
			UserEntity userInfo = snsLogin("G",tokenDto.getEmail(),null,tokenDto.getName());
			if(userInfo == null) {
				throw new Exception("로그인에러");
			}
			
			JwtDTO jwtDto = JwtDTO.builder()
								.access_token(null)
								.expires_in(10800L)
								.id_token(null)
								.refresh_token(null)
								.refresh_token_expires_in(10800L)
								.sid(userInfo.getSid())
								.type(userInfo.getLogin_type())
								.build();
			
			String jwtToken = jwtTokenProvider.generateToken(jwtDto);
			
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
										.errFlag(false)
										.resDate(new Date())
										.data(jwtToken)
										.build();
			
			return ret;
		}catch (Exception e) {
			// TODO: handle exception
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(new Date())
					.data(e.getMessage())
					.build();
			
			log.error("UserService.184 : {}",e);
			
			return ret;
		}
	}
	
	public ResponseDTO<String> naverLogin(String code){
		
		try {
			Mono<String> authToken = apiService.naverAuthToken(code);
			if(authToken == null) {
				throw new Exception("네이버로그인 에러");
			}
			NaverTokenDTO authDto = UtilService.parseJsonToDto(authToken.block(), NaverTokenDTO.class);
			if(authDto == null) {
				throw new Exception("네이버로그인 에러");
			}
			
			Mono<String> infoJson = apiService.naverInfo(authDto.getAccess_token());
			
			if(infoJson == null) {
				throw new Exception("네이버로그인 에러");
			}
			
			NaverInfoDTO infoDto = UtilService.parseJsonToDto(infoJson.block(), NaverInfoDTO.class);
			
			if(infoDto==null|| !infoDto.getMessage().equals("success")) {
				throw new Exception("네이버로그인 에러");
			}
			
			String[] parts = infoDto.getResponse().getBirthday().split("-");
			
			String birthDate = infoDto.getResponse().getBirthyear()+parts[0]+parts[1];
			
			UserEntity userInfo = snsLogin("N",infoDto.getResponse().getEmail(),authDto.getAccess_token(),infoDto.getResponse().getName());
			if(userInfo == null) {
				throw new Exception("로그인에러");
			}
			userInfo.setDateBirth(birthDate);
			userInfo = userRepository.save(userInfo);
			
			JwtDTO jwtDto = JwtDTO.builder()
					.access_token(userInfo.getPassword())
					.expires_in(authDto.getExpires_in())
					.id_token(null)
					.refresh_token(authDto.getRefresh_token())
					.refresh_token_expires_in(authDto.getExpires_in())
					.sid(userInfo.getSid())
					.type(userInfo.getLogin_type())
					.build();

			String jwtToken = jwtTokenProvider.generateToken(jwtDto);
			
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
										.errFlag(false)
										.resDate(new Date())
										.data(jwtToken)
										.build();
			return ret;
			
		}catch (Exception e) {
			// TODO: handle exception
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(new Date())
					.data(e.getMessage())
					.build();
			
			log.error("UserService.251 : {}",e);
			
			return ret;
		}
		
		
	}
	
}
