package com.wipo.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wipo.Appconfig.JwtTokenProvider;
import com.wipo.DTO.EmailAuthCodeDTO;
import com.wipo.DTO.GoogleInfoDTO;
import com.wipo.DTO.GoogleTokenDTO;
import com.wipo.DTO.JwtDTO;
import com.wipo.DTO.KakaoTokenDTO;
import com.wipo.DTO.KakaoUserDTO;
import com.wipo.DTO.ResponseDTO;
import com.wipo.DTO.UserSignDTO;
import com.wipo.Entity.UserEntity;
import com.wipo.Entity.UserRelationEntity;
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
	@Autowired
	private RelationService relationService;
	
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
								.type(userEntities.getLogintype())
								.build();
			
			String jwtToken = jwtTokenProvider.generateToken(jwtDto);
			
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
										.errFlag(false)
										.resDate(ZonedDateTime.now())
										.data(jwtToken)
										.build();
			
			return ret;
		}catch (Exception e) {
			// TODO: handle exception
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
			
			log.error("UserService.kakaoLogin : {}",e);
			
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
				userEntities.setPrivacy(true);
				
			}else {
				userEntities = UserEntity.builder()
								.create_at(ZonedDateTime.now())
								.email(email)
								.isPrivacy(true)
								.logintype(login_type) // type kakao:K google:G
								.name(name)
								.password(access_token)
								.build();
				
			}
			
			return userRepository.save(userEntities);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserService.snsLogin : {}",e);
			return null;
			
		}
	}
	
	public ResponseDTO<String> googleLogin(String credential){
		try {
			Mono<String> apiJson = apiService.googleToken(credential);
			
			if(apiJson == null) {
				throw new Exception("구글로그인 에러");
			}
			
			GoogleTokenDTO tokenDto = UtilService.parseJsonToDto(apiJson.block(), GoogleTokenDTO.class);
			
			if(tokenDto==null) {
				throw new Exception("구글로그인 에러");
			}
			
			Mono<String> infoJson = apiService.googleInfo(tokenDto.getAccess_token());
			if(infoJson == null) {
				throw new Exception("구글정보조회 에러");
			}
			
			GoogleInfoDTO infoDto = UtilService.parseJsonToDto(infoJson.block(), GoogleInfoDTO.class);
			
			if(infoDto == null) {
				throw new Exception("구글정보조회 에러");
			}
			UserEntity userInfo = snsLogin("G",infoDto.getEmail(),tokenDto.getAccess_token(),infoDto.getName());
			if(userInfo == null) {
				throw new Exception("로그인에러");
			}
			
			JwtDTO jwtDto = JwtDTO.builder()
			.access_token(tokenDto.getAccess_token())
			.expires_in(tokenDto.getExpires_in())
			.id_token(tokenDto.getId_token())
			.refresh_token(tokenDto.getRefresh_token())
			.refresh_token_expires_in(tokenDto.getExpires_in())
			.sid(userInfo.getSid())
			.type(userInfo.getLogintype())
			.build();

			String jwtToken = jwtTokenProvider.generateToken(jwtDto);

			ResponseDTO<String> ret = ResponseDTO.<String>builder()
					.errFlag(false)
					.resDate(ZonedDateTime.now())
					.data(jwtToken)
					.build();

			return ret;
		}catch (Exception e) {
			// TODO: handle exception
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
			
			log.error("UserService.googleLogin : {}",e);
			
			return ret;
		}
	}
	
	public ResponseDTO<?> emailValid(String email, String code){
		try {
			boolean errFlag = UtilService.validateAuthEmail(email, code);
			
			if(errFlag) {
				throw new Exception("인증실패");
			}
			
			return ResponseDTO.<Boolean>builder()
					.errFlag(errFlag)
					.resDate(ZonedDateTime.now())
					.build();
		}catch (Exception e) {
			// TODO: handle exception
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
			
			log.error("UserService.emailValid : {}",e);
			
			return ret;
		}
	}
	
	public ResponseDTO<?> emailAuth(String email){
		
		try {

			EmailAuthCodeDTO ret = UtilService.generateAndSaveCode(email);
			
			String emailBody = """
	                   <!DOCTYPE html>
	                   <html lang="ko">
	                   <head>
	                       <meta charset="UTF-8">
	                       <meta name="viewport" content="width=device-width, initial-scale=1.0">
	                       <title>Verification Code</title>
	                       <link href="https://hangeul.pstatic.net/hangeul_static/css/nanum-square-neo.css" rel="stylesheet">
	                   </head>
	                   <body style="font-family: nanum-square-neo, sans-serif; line-height: 1.6; background-color: #f4f4f9; padding: 20px;">

	                       <div style="max-width: 50vw; margin: 0 auto; background: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);">
	                           <header style="background-color: #E0E7FF; padding: 10px; text-align: center; color: white;">
	                            <img src="https://wipo-front-buck.s3.ap-northeast-2.amazonaws.com/static/media/Logo.69bfbfa08f08bc092067.png" alt="Logo" style="margin-top: 10px; width: 10vw; height: auto;"/>
	                        </header>
	                           <main style="padding: 20px;">
	                               <h2 style="color: #333;">이메일 인증코드</h2>
	                               <p>당신만의 이야기를 기록할 수 있는 <b>WiPo </b>서비스에 가입하신 것을 환영합니다!</p>
	                               <p>아래의 인증코드를 입력하시면 가입이 정상적으로 완료됩니다.</p>
	                               <div style="margin: 5vh 0; text-align: center;">
	                                   <span style="font-size: 36px; color: #364474; font-weight: bold; border: 2px solid #364474; padding: 1vh 2vw; border-radius: 10px;">"""+ret.getCode()+"""
	                                   </span>
	                               </div>
	                               <p><b>3분</b> 내에 인증코드를 입력해주세요.</p>
	                               <p>궁금한 점이 있거나 도움이 필요하면 언제든지 저희에게 문의해주세요.</p>
	                               <p style="margin-top: 5vh;">감사합니다.</p>
	                               <p style="font-weight: bold;">Wipo 팀 드림</p>
	                           </main>
	                           <footer style="background-color: #E0E7FF; padding: 10px 20px; text-align: center; font-size: 12px; color: #777;">
	                               <p>© 2024 WiPo. All rights reserved.</p>
	                           </footer>
	                       </div>

	                   </body>
	                   </html>
	""";
			
			boolean errFlag = UtilService.sendEmail(email, "이메일보안인증", emailBody);
			if(errFlag) {
				throw new Exception("이메일전송에러");
			}
			
			return ResponseDTO.<LocalDateTime>builder()
					.errFlag(errFlag)
					.data(ret.getExpiresAt())
					.resDate(ZonedDateTime.now())
					.build();
					
		}catch (Exception e) {
			// TODO: handle exception
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
			
			log.error("UserService.emailAuth : {}",e);
			
			return ret;
			
		}
		
	}
	
	public ResponseDTO<?> asign(UserSignDTO dto){
		try {
			
			UserEntity userEntities = userRepository.findByEmail(dto.getEmail());
			
			if(userEntities != null) {
				throw new Exception("이미 가입한 이메일입니다.");
			}
			
			userEntities = UserEntity.builder()
													.create_at(ZonedDateTime.now())
													.dateBirth(dto.getBirthDate())
													.email(dto.getEmail())
													.isPrivacy(true)
													.logintype("W")
													.name(dto.getName())
													.password(dto.getPassword())
													.build();
			userRepository.save(userEntities);
			
			return ResponseDTO.<String>builder()
					.errFlag(false)
					.resDate(ZonedDateTime.now())
					.build();
		}catch (Exception e) {
			// TODO: handle exception
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
			
			log.error("UserService.asign : {}",e);
			
			return ret;
		}
	}
	
	public ResponseDTO<?> login(Map<String, String> dto){
		try {
			String email = dto.get("email");
			if(email == null) {
				throw new Exception("이메일 에러");
			}
			String password = dto.get("password");
			if(password == null) {
				throw new Exception("패스워드 에러");
			}
			Optional<UserEntity> userEntities = userRepository.findByEmailAndPassword(email, password);
			
			if(!userEntities.isPresent()) {
				throw new Exception("정보 없음");
			}
			
			JwtDTO jwtDto = JwtDTO.builder()
					.access_token(null)
					.expires_in(10800L)
					.id_token(null)
					.refresh_token(null)
					.refresh_token_expires_in(10800L)
					.sid(userEntities.get().getSid())
					.type(userEntities.get().getLogintype())
					.build();

			String jwtToken = jwtTokenProvider.generateToken(jwtDto);
			
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
					.errFlag(false)
					.resDate(ZonedDateTime.now())
					.data(jwtToken)
					.build();

			return ret;
			
		}catch (Exception e) {
			// TODO: handle exception
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
			
			log.error("UserService.login : {}",e);
			
			return ret;
		}
	}
	
	
	public ResponseDTO<?> userInfo(String userJson){
		try {
			JwtDTO jwtDto = UtilService.parseJsonToDto(userJson, JwtDTO.class);
			if(jwtDto==null) {
				throw new Exception("토큰에러");
			}
			UserEntity userEntity = userRepository.findById(jwtDto.getSid()).orElse(null);
			if(userEntity==null) {
				throw new Exception("유저에러");
			}
			
			userEntity.setCreate_at(null);
			userEntity.setPassword(null);
			
			return ResponseDTO.<UserEntity>builder()
																.errFlag(false)
																.data(userEntity)
																.resDate(ZonedDateTime.now())
																.build();
					
		}catch (Exception e) {
			// TODO: handle exception
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
			
			log.error("UserService.userInfo : {}",e);
			
			return ret;
		}
	}

	public ResponseDTO<?> userSaveNameBirth(String userJson,String name,String dateBirth){
		try {
			JwtDTO jwtDto = UtilService.parseJsonToDto(userJson, JwtDTO.class);
			if(jwtDto==null) {
				throw new Exception("토큰에러");
			}
			UserEntity userEntity = userRepository.findById(jwtDto.getSid()).orElse(null);
			if(userEntity==null) {
				throw new Exception("유저에러");
			}
			if(dateBirth!=null) {
				userEntity.setDateBirth(dateBirth);
			}
			if(name!=null) {
				userEntity.setName(name);
			}
			userRepository.save(userEntity);
			return ResponseDTO.<UserEntity>builder()
					.errFlag(false)
					.data(userEntity)
					.resDate(ZonedDateTime.now())
					.build();
		}catch (Exception e) {
			// TODO: handle exception
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
			
			log.error("UserService.userSaveNameBirth : {}",e);
			
			return ret;
		}
	}
	
	public ResponseDTO<String> findEmail(String name,String dateBirth){
		try {
			UserEntity userEntities = userRepository.findByNameAndDateBirthAndLogintype(name, dateBirth,"W").orElse(null);
			if(userEntities==null) {
				throw new Exception("사용자정보가 없습니다");
			}
			return ResponseDTO.<String>builder()
					.errFlag(false)
					.data(userEntities.getEmail())
					.resDate(ZonedDateTime.now())
					.build(); 
		}catch (Exception e) {
			// TODO: handle exception
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
			
			log.error("UserService.findEmail : {}",e);
			
			return ret;
		}
	}

	public ResponseDTO<?> findPass(String email,String name){
		try {
			UserEntity userEntities = userRepository.findByEmailAndNameAndLogintype(email, name,"W").orElse(null);
			if(userEntities==null) {
				throw new Exception("사용자정보가 없습니다");
			}
			
			ResponseDTO<?> emailRet = emailAuth(userEntities.getEmail());
			
			if(emailRet.isErrFlag()) {
				throw new Exception("이메일 전송에러");
			}
			
			return ResponseDTO.builder()
					.errFlag(false)
					.data(emailRet.getData())
					.resDate(ZonedDateTime.now())
					.build(); 
		}catch (Exception e) {
			// TODO: handle exception
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
			
			log.error("UserService.findPass : {}",e);
			
			return ret;
		}
	}

	public ResponseDTO<?> changePass(String email,String password){
		try {
			UserEntity userEntities = userRepository.findByEmailAndLogintype(email, "W").orElse(null);
			if(userEntities==null) {
				throw new Exception("사용자정보가 없습니다");
			}
			
			userEntities.setPassword(password);
			userEntities = userRepository.save(userEntities);
			
			return ResponseDTO.builder()
					.errFlag(false)
					.resDate(ZonedDateTime.now())
					.build(); 
		}catch (Exception e) {
			// TODO: handle exception
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
			
			log.error("UserService.findPass : {}",e);
			
			return ret;
		}
	}
	
	@Transactional
	public ResponseDTO<?> setFriendInfo(Long  userSid,List<Long> friendSid){
		try {
			List<UserEntity> friendArray = new ArrayList<UserEntity>();
			UserEntity userEntity = userRepository.findById(userSid).orElse(null);
			if(userEntity==null) {
				throw new Exception("사용자에러");
			}
			for(Long row: friendSid) {
				UserEntity friendEntity = userRepository.findById(row).orElse(null);
				if(friendEntity ==null) {
					throw new Exception("팔로우목록이 잘못되었습니다.");
				}
				UserRelationEntity relEntity = relationService.setUserRelationSave(userEntity, friendEntity);
				if(relEntity==null) {
					throw new Exception("팔로우목록이 잘못되었습니다.");
				}
				friendArray.add(friendEntity);
			}
			return ResponseDTO.<List<UserEntity>>builder()
					.data(friendArray)
					.errFlag(false)
					.resDate(ZonedDateTime.now())
					.build();
					
		}catch (Exception e) {
			// TODO: handle exception
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
			
			log.error("UserService.setFriendInfo : {}",e);
			
			return ret;
		}
	}
	
}
