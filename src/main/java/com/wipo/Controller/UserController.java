package com.wipo.Controller;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.wipo.Appconfig.JwtTokenProvider;
import com.wipo.DTO.JwtDTO;
import com.wipo.DTO.ResponseDTO;
import com.wipo.DTO.UserSignDTO;
import com.wipo.Entity.UserEntity;
import com.wipo.Service.AlertService;
import com.wipo.Service.UserService;
import com.wipo.Service.UtilService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("user")
@RestController
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private AlertService alertService;
	
	@GetMapping("/")
	public ResponseEntity<?> testApi(){
		return new ResponseEntity<>("Hello World2",HttpStatus.OK); 
	}
	
	//카카오 로그인
	@GetMapping("/kakaoLogin")
	public ResponseEntity<?> kakaoLogin(@RequestParam("code")String code) {
		
		try {
			ResponseDTO<String> ret = userService.kakaoLogin(code);
			if(ret.isErrFlag()) {
				return new ResponseEntity<>(ret,HttpStatus.BAD_REQUEST);
			}else {
				return new ResponseEntity<>(ret,HttpStatus.OK);
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.kakaoLogin : {}",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
		
	}
	
	//구글로그인
	@GetMapping("/googleLogin")
	public ResponseEntity<?> googleLogin(@RequestParam("code")String code){
		try {
			ResponseDTO<String> ret = userService.googleLogin(code);
			if(ret.isErrFlag()) {
				return new ResponseEntity<>(ret,HttpStatus.BAD_REQUEST);
			}else {
				return new ResponseEntity<>(ret,HttpStatus.OK);
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.googleLogin : {} ",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
	}

	//이메일 코드발급
	@GetMapping("/emailAuth")
	public ResponseEntity<?> emailAuth(@RequestParam("email")String email){
		try {
			ResponseDTO<?> ret = userService.emailAuth(email);
			
			if(ret.isErrFlag()) {
				return new ResponseEntity<>(ret,HttpStatus.BAD_REQUEST);
			}else {
				return new ResponseEntity<>(ret,HttpStatus.OK);
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.emailAuth : {}",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
		
	}
	//이메일 인증
	@GetMapping("/emailValid")
	public ResponseEntity<?> emailValid(@RequestParam("email")String email,@RequestParam("code")String code){
		try {
			ResponseDTO<?> ret = userService.emailValid(email, code);
			if(ret.isErrFlag()) {
				return new ResponseEntity<>(ret,HttpStatus.BAD_REQUEST);
			}else {
				return new ResponseEntity<>(ret,HttpStatus.OK);
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.emailValid : {}",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
		
	}
	
	//일반회원가입
	@PostMapping("/asign")
	public ResponseEntity<?> asign(@RequestBody UserSignDTO dto){
		try {
			ResponseDTO<?> ret = userService.asign(dto);
			if(ret.isErrFlag()) {
				return new ResponseEntity<>(ret,HttpStatus.BAD_REQUEST);
			}else {
				return new ResponseEntity<>(ret,HttpStatus.OK);
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.asign : {}",e);
			
			return ResponseEntity.badRequest().body("회원가입서버에러");
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> dto){
		try {
			ResponseDTO<?> ret = userService.login(dto);
			if(ret.isErrFlag()) {
				return new ResponseEntity<>(ret,HttpStatus.BAD_REQUEST);
			}else {
				return new ResponseEntity<>(ret,HttpStatus.OK);
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.login : {}",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
	}
	
	@GetMapping("/userInfo")
	public ResponseEntity<?> userInfo(@RequestHeader("Authorization") String jwt){
		try {
			if(!jwtTokenProvider.validateJwt(jwt)) {
				Claims claims = jwtTokenProvider.validateToken(jwt);
				String json = claims.get("user",String.class);
				
				ResponseDTO<?> ret = userService.userInfo(json);
				if(ret.isErrFlag()) {
					return new ResponseEntity<>(ret,HttpStatus.BAD_REQUEST);
				}else {
					return new ResponseEntity<>(ret,HttpStatus.OK);
				}
				
			}else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰에러");
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.userInfo : {} ",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
	}
	
	@GetMapping("/saveNameBirth")
	public ResponseEntity<?> saveNameBirth(@RequestHeader("Authorization") String jwt,
																	@RequestParam(required = false,name = "name")String name,
																	@RequestParam(required = false,name="dateBirth")String dateBirth){
		try {
			if(!jwtTokenProvider.validateJwt(jwt)) {
				Claims claims = jwtTokenProvider.validateToken(jwt);
				String json = claims.get("user",String.class);
				
				ResponseDTO<?> ret = userService.userSaveNameBirth(json, name, dateBirth);
				if(ret.isErrFlag()) {
					return new ResponseEntity<>(ret,HttpStatus.BAD_REQUEST);
				}else {
					return new ResponseEntity<>(ret,HttpStatus.OK);
				}
				
			}else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰에러");
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.userInfo : {}",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
	}

	@GetMapping("/findEmail")
	public ResponseEntity<?> findEmail(@RequestParam(required = true,name = "name")String name,
															@RequestParam(required = true,name="dateBirth")String dateBirth){
		try {
			ResponseDTO<String> ret = userService.findEmail(name, dateBirth);
			if(ret.isErrFlag()) {
				return new ResponseEntity<>(ret,HttpStatus.BAD_REQUEST);
			}else {
				return new ResponseEntity<>(ret,HttpStatus.OK);
			}
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.findEmail : {}",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
	}
	@GetMapping("/findPass")
	public ResponseEntity<?> findPass(@RequestParam(required = true,name = "email")String email,
															@RequestParam(required = true,name="name")String name){
		try {
			ResponseDTO<?> ret = userService.findPass(email, name);
			if(ret.isErrFlag()) {
				return new ResponseEntity<>(ret,HttpStatus.BAD_REQUEST);
			}else {
				return new ResponseEntity<>(ret,HttpStatus.OK);
			}
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.findPass : {} ",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
	}
	
	@GetMapping("/changePass")
	public ResponseEntity<?> changePass(@RequestParam(required = true,name = "email")String email,
															@RequestParam(required = true,name="password")String password){
		try {
			ResponseDTO<?> ret = userService.changePass(email, password);
			if(ret.isErrFlag()) {
				return new ResponseEntity<>(ret,HttpStatus.BAD_REQUEST);
			}else {
				return new ResponseEntity<>(ret,HttpStatus.OK);
			}
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.changePass : {}",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
	}
	
	@GetMapping(value="/stream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<SseEmitter> setStream(@RequestParam("jwtToken")String jwtToken) {

		if(!jwtTokenProvider.validateJwt(jwtToken)) {
			Claims claims = jwtTokenProvider.validateToken(jwtToken);
			String json = claims.get("user",String.class);
			
			JwtDTO jwtDto = UtilService.parseJsonToDto(json, JwtDTO.class);
			
	        SseEmitter ret = alertService.setClients(jwtDto.getSid());
	        return new ResponseEntity<>(ret,HttpStatus.OK);
		}else {
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(value="/disconStream")
	public ResponseEntity<?> setDisconStream(@RequestParam("jwtToken")String jwtToken){
		if(!jwtTokenProvider.validateJwt(jwtToken)) {
			Claims claims = jwtTokenProvider.validateToken(jwtToken);
			String json = claims.get("user",String.class);
			
			JwtDTO jwtDto = UtilService.parseJsonToDto(json, JwtDTO.class);
			
	        alertService.disconStream(jwtDto.getSid());
	        return new ResponseEntity<>("",HttpStatus.OK);
		}else {
			return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/getFindByUser")
	public ResponseEntity<?> getFindByUser(@RequestHeader("Authorization") String jwt,@RequestParam("str")String str){
		try {
			if(!jwtTokenProvider.validateJwt(jwt)) {
				Claims claims = jwtTokenProvider.validateToken(jwt);
				String json = claims.get("user",String.class);
				JwtDTO jwtDto = UtilService.parseJsonToDto(json, JwtDTO.class);
				ResponseDTO<?> ret = userService.getFindByUser(jwtDto.getSid(), str);
				if(ret.isErrFlag()) {
					return new ResponseEntity<>(ret,HttpStatus.BAD_REQUEST);
				}else {
					return new ResponseEntity<>(ret,HttpStatus.OK);
				}
				
			}else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰에러");
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.getFindByUser : {}",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
	}
	
	@GetMapping("/setFriendUser")
	public ResponseEntity<?> setFriendUser(@RequestHeader("Authorization") String jwt,@RequestParam("friendSid")Long friendSid){
		try {
			if(!jwtTokenProvider.validateJwt(jwt)) {
				Claims claims = jwtTokenProvider.validateToken(jwt);
				String json = claims.get("user",String.class);
				JwtDTO jwtDto = UtilService.parseJsonToDto(json, JwtDTO.class);
				ResponseDTO<?> ret = userService.setFriendUser(jwtDto.getSid(),friendSid);
				if(ret.isErrFlag()) {
					return new ResponseEntity<>(ret,HttpStatus.BAD_REQUEST);
				}else {
					return new ResponseEntity<>(ret,HttpStatus.OK);
				}
				
			}else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰에러");
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.setFriendUser : {}",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
	}
	
	@GetMapping("/setRelApprove")
	public ResponseEntity<?> setRelApprove(@RequestHeader("Authorization") String jwt,@RequestParam("sid")Long sid,@RequestParam("approveFlag")String approveFlag){
		try {
			if(!jwtTokenProvider.validateJwt(jwt)) {
				ResponseDTO<?> ret = userService.setRelApprove(sid, approveFlag);
				if(ret.isErrFlag()) {
					return new ResponseEntity<>(ret,HttpStatus.BAD_REQUEST);
				}else {
					return new ResponseEntity<>(ret,HttpStatus.OK);
				}
				
			}else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰에러");
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.setRelApprove : {}",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
	}
	
	
	@GetMapping("/getUserRelInfo")
	public ResponseEntity<?> getUserRelInfo(@RequestHeader("Authorization") String jwt, @RequestParam("sid")Long sid){
		try {
			if(!jwtTokenProvider.validateJwt(jwt)) {
				Claims claims = jwtTokenProvider.validateToken(jwt);
				String json = claims.get("user",String.class);
				
				JwtDTO jwtDto = UtilService.parseJsonToDto(json, JwtDTO.class);
				ResponseDTO<?> ret = userService.getUserRelInfo(sid,jwtDto.getSid());
				if(ret.isErrFlag()) {
					return new ResponseEntity<>(ret,HttpStatus.BAD_REQUEST);
				}else {
					return new ResponseEntity<>(ret,HttpStatus.OK);
				}
				
			}else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰에러");
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.getUserRelInfo : {}",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
	}
	
	@PostMapping(value="/setProfile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> setProfile(@RequestHeader("Authorization") String jwt,
															@RequestPart(value="dateBirth",required = false)String dateBirth,
															@RequestPart(value="color",required = false)String color,
															@RequestPart(value="image",required = false)MultipartFile image){
		try {
			if(!jwtTokenProvider.validateJwt(jwt)) {
				Claims claims = jwtTokenProvider.validateToken(jwt);
				String json = claims.get("user",String.class);
				
				JwtDTO jwtDto = UtilService.parseJsonToDto(json, JwtDTO.class);
				ResponseDTO<?> ret = userService.setProfile(jwtDto.getSid(), dateBirth, color, image);
				if(ret.isErrFlag()) {
					return new ResponseEntity<>(ret,HttpStatus.BAD_REQUEST);
				}else {
					return new ResponseEntity<>(ret,HttpStatus.OK);
				}
			}else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰에러");
			}
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.getUserRelInfo : {}",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
	}
	
}
