package com.wipo.Service;

import java.lang.annotation.Retention;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wipo.Appconfig.JwtTokenProvider;
import com.wipo.DTO.AlertSendDTO;
import com.wipo.DTO.EmailAuthCodeDTO;
import com.wipo.DTO.GoogleInfoDTO;
import com.wipo.DTO.GoogleTokenDTO;
import com.wipo.DTO.JwtDTO;
import com.wipo.DTO.KakaoTokenDTO;
import com.wipo.DTO.KakaoUserDTO;
import com.wipo.DTO.ResponseDTO;
import com.wipo.DTO.SendUserInfoDTO;
import com.wipo.DTO.UserSignDTO;
import com.wipo.Entity.FileEntity;
import com.wipo.Entity.MapEntity;
import com.wipo.Entity.UserEntity;
import com.wipo.Entity.UserRelationEntity;
import com.wipo.Repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
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
	
	private final FileService fileService;
	
	public UserService(FileService fileService) {
		this.fileService = fileService;
	}
	
	
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
										.data(jwtToken+";"+jwtDto.getRefresh_token_expires_in().toString())
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
	
	@Transactional
	private UserEntity snsLogin(String login_type,
								String email,
								String access_token,
								String name) {
		try {
			log.info(email+":"+login_type+":"+"로그인");
			UserEntity userEntities = userRepository.findByEmail(email);
			
			if(userEntities != null) {
				userEntities.setPassword(access_token);
				userEntities.setPrivacy(true);
				if(userEntities.getFriendsLength()==null) {
					userEntities.setFriendsLength(0);
				}
				
			}else {
				userEntities = UserEntity.builder()
								.create_at(ZonedDateTime.now())
								.email(email)
								.isPrivacy(true)
								.logintype(login_type) // type kakao:K google:G
								.name(name)
								.friendsLength(0)
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
					.data(jwtToken+";"+jwtDto.getRefresh_token_expires_in().toString())
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

			UserEntity userEntity = userRepository.findByEmail(email);
			if(userEntity!=null) {
				throw new Exception("이미 가입한 유저입니다.");
			}
			
			
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
													.friendsLength(0)
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
			log.info(dto.get("email")+"로그인");
			String email = dto.get("email");
			if(email == null) {
				throw new Exception("이메일 에러");
			}
			String password = dto.get("password");
			if(password == null) {
				throw new Exception("패스워드 에러");
			}
			UserEntity userEntities = userRepository.findByEmail(email);
			
			if(userEntities==null) {
				throw new Exception("회원가입요망");
			}
			
			if(!userEntities.getPassword().equals(password)) {
				throw new Exception("패스워드 에러");
			}
			
			
			JwtDTO jwtDto = JwtDTO.builder()
					.access_token(null)
					.expires_in(10800L)
					.id_token(null)
					.refresh_token(null)
					.refresh_token_expires_in(10800L)
					.sid(userEntities.getSid())
					.type(userEntities.getLogintype())
					.build();

			String jwtToken = jwtTokenProvider.generateToken(jwtDto);
			
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
					.errFlag(false)
					.resDate(ZonedDateTime.now())
					.data(jwtToken+";"+jwtDto.getRefresh_token_expires_in().toString())
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
			log.info(userEntity.getSid()+"유저정보조회");
			userEntity.setPassword(null);
			
		    List<UserRelationEntity> friendRelArray = relationService.getUserRelInfo(userEntity);
		    List<UserRelationEntity> meRelArray = relationService.getFriendRelInfo(userEntity);
		    List<UserEntity> friendUserArray = new ArrayList<UserEntity>();
		    
		    for(UserRelationEntity row: friendRelArray) {
		    	UserEntity tempEntity = row.getFriend();
		    	tempEntity.setPassword(null);
		    	friendUserArray.add(tempEntity);
		    }
		    
		    for(UserRelationEntity row:meRelArray) {
		    	UserEntity tempEntity = row.getUser();
		    	tempEntity.setPassword(null);
		    	friendUserArray.add(tempEntity);
		    }
		    //즐겨찾기 맵
		    List<MapEntity> favList = relationService.getMapRelFav(userEntity);
		    
		    SendUserInfoDTO dto = SendUserInfoDTO.builder()
		    									.user(userEntity)
		    									.friend(friendUserArray)
		    									.favList(favList)
		    									.build();
		    
			return ResponseDTO.<SendUserInfoDTO>builder()
																.errFlag(false)
																.data(dto)
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
			userEntity = userRepository.save(userEntity);
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
	
	public ResponseDTO<?> setFriendUser(Long userSid,Long friendSid){
		try {
			UserEntity userEntity = userRepository.findById(userSid).orElse(null);
			UserEntity friendEntity = userRepository.findById(friendSid).orElse(null);
			if(friendEntity==null) {
				throw new Exception("사용자정보가 없습니다");
			}
			UserRelationEntity relEntity = relationService.setUserRelationSave(userEntity, friendEntity,"W");
			if(relEntity==null) {
				throw new Exception("이미 추가한 친구입니다.");
			}
			return ResponseDTO.<UserEntity>builder()
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
			
			log.error("UserService.setFriendUser : {}",e);
			
			return ret;
		}
	}
	
	public ResponseDTO<?> getFindByUser(Long userSid,String str){
		try {
			UserEntity userEntity = userRepository.findById(userSid).orElse(null);
			if(userEntity ==null) {
				throw new Exception("유저정보에러");
			}
			//내가보낸것
			List<UserRelationEntity> userTmpArray = relationService.getUserRelInfo(userEntity);
			List<UserRelationEntity> friendTmpArray = relationService.getFriendRelInfo(userEntity);
			List<UserEntity> tempArray = new ArrayList<UserEntity>();
			for(UserRelationEntity row : userTmpArray) {
				row.getFriend().setPassword(null);
				tempArray.add(row.getFriend());
			}
			
			for(UserRelationEntity row: friendTmpArray) {
				row.getUser().setPassword(null);
				tempArray.add(row.getUser());
			}
			
			
			List<UserEntity> userArray = userRepository.findByUser(str);
			userArray.removeIf(user->userEntity.getSid().equals(user.getSid()));
			for(UserEntity row: tempArray) {
				
				userArray.removeIf(user->row.getSid().equals(user.getSid()));
			}
			
			return ResponseDTO.<List<UserEntity>>builder()
					.errFlag(false)
					.resDate(ZonedDateTime.now())
					.data(userArray)
					.build();
			
		}catch (Exception e) {
			// TODO: handle exception
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
			
			log.error("UserService.getFindByUser : {}",e);
			
			return ret;
		}
	}
	
	public ResponseDTO<?> getUserRelInfo(Long paramSid,Long userSid){
		try {
			UserRelationEntity relEntity = relationService.getRelInfo(paramSid);
			if(relEntity == null) {
				throw new Exception("친구정보에러");
			}
			UserEntity userEntity = null;
			if(relEntity.getUser().getSid()==userSid) {
				
				userEntity = relEntity.getFriend();
			}else {
				userEntity = relEntity.getUser();
			}
			
			userEntity.setPassword(null);
			return ResponseDTO.<UserEntity>builder()
					.errFlag(false)
					.resDate(ZonedDateTime.now())
					.data(userEntity)
					.build();
		}catch (Exception e) {
			// TODO: handle exception
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
			
			log.error("UserService.getUserRelInfo : {}",e);
			
			return ret;
		}
	}
	
	
	public ResponseDTO<?> setRelApprove(Long paramSid, String approveFlag){
		try {
			String str = "";
			UserRelationEntity relEntity = relationService.setRelApprove(paramSid, approveFlag);
			
			if(relEntity==null) {
				throw new Exception("저장에러");
			}
			
			relEntity.getFriend().setPassword(null);
			relEntity.getUser().setPassword(null);
			
			SseEmitter client = AlertService.validClient(relEntity.getUser().getSid());
			if(client!= null) {
				if(relEntity.getApprove_flag().equals("Y")){
					str = "님이 친구 요청을 수락했습니다.";
				}else {
					str = "님이 친구 요청을 거절했습니다.";
				}
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd");
				AlertSendDTO dto = AlertSendDTO.builder()
												.approve_flag(relEntity.getApprove_flag())
												.confirm_flag(relEntity.getConfirm_flag())
												.content(relEntity.getFriend().getName()+str)
												.date(relEntity.getUpdate_at().format(formatter))
												.sid(relEntity.getSid())
												.title(relEntity.getFriend().getEmail())
												.type("F")
												.build();
				
				
				
				ObjectMapper mapper = new ObjectMapper();
				String retJson = mapper.writeValueAsString(dto);
				String friendJson = mapper.writeValueAsString(relEntity.getFriend());
				client.send(SseEmitter.event().name("alert").data(retJson));
				client.send(SseEmitter.event().name("friend").data(friendJson));
			}
			return ResponseDTO.<UserEntity>builder()
					.errFlag(false)
					.data(relEntity.getUser())
					.resDate(ZonedDateTime.now())
					.build();
			
		}catch (Exception e) {
			// TODO: handle exception
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
			
			log.error("UserService.setRelApprove : {}",e);
			
			return ret;
		}
	}
	
	public ResponseDTO<?> setProfile(Long userSid,String dateBirth,String color,MultipartFile file){
		try {
			FileEntity fileEntity  = null;
			if(file!=null) {
				fileEntity = fileService.setFileDBSaveOne(file);
				if(fileEntity==null) {
					throw new Exception("파일 저장에러");
				}
			}
			
			UserEntity userEntity = userRepository.findById(userSid).orElse(null);
			if(userEntity == null) {
				throw new Exception("새로고침 필요");
			}
			userEntity.setDateBirth(dateBirth);
			userEntity.setProfileColor(color);
			userEntity.setFile(fileEntity);
			userEntity = userRepository.save(userEntity);
			userEntity.setPassword(null);
			
			return ResponseDTO.<UserEntity>builder()
					.errFlag(false)
					.resDate(ZonedDateTime.now())
					.data(userEntity)
					.build();
		}catch (Exception e) {
			// TODO: handle exception
			ResponseDTO<String> ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
			
			log.error("UserService.setProfile : {}",e);
			
			return ret;
		}
	}
}
