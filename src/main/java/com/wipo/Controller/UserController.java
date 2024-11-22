package com.wipo.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wipo.DTO.ResponseDTO;
import com.wipo.DTO.UserSignDTO;
import com.wipo.Service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("user")
@RestController
public class UserController {

	@Autowired
	private UserService userService;
	
	
	@GetMapping("/")
	public ResponseEntity<?> testApi(){
		return new ResponseEntity<>("Hello World",HttpStatus.OK); 
	}
	
	//카카오 로그인
	@GetMapping("/kakaoLogin")
	public ResponseEntity<?> kakaoLogin(@RequestParam("code")String code) {
		
		try {
			ResponseDTO<String> ret = userService.kakaoLogin(code);
			return new ResponseEntity<>(ret,HttpStatus.OK); 
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.kakaoLogin : ",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
		
	}
	
	//구글로그인
	@GetMapping("/googleLogin")
	public ResponseEntity<?> googleLogin(@RequestParam("code")String code){
		try {
			ResponseDTO<String> ret = userService.googleLogin(code);
			return new ResponseEntity<>(ret,HttpStatus.OK); 
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.googleLogin : ",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
	}

	//이메일 코드발급
	@GetMapping("/emailAuth")
	public ResponseEntity<?> emailAuth(@RequestParam("email")String email){
		try {
			ResponseDTO<?> ret = userService.emailAuth(email);
			
			return new ResponseEntity<>(ret,HttpStatus.OK); 
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.emailAuth : ",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
		
	}
	//이메일 인증
	@GetMapping("/emailValid")
	public ResponseEntity<?> emailValid(@RequestParam("email")String email,@RequestParam("code")String code){
		try {
			ResponseDTO<?> ret = userService.emailValid(email, code);
			return new ResponseEntity<>(ret,HttpStatus.OK); 
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.emailValid : ",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
		
	}
	
	//일반회원가입
	@PostMapping("/asign")
	public ResponseEntity<?> asign(@RequestBody UserSignDTO dto){
		try {
			ResponseDTO<?> ret = userService.asign(dto);
			return new ResponseEntity<>(ret,HttpStatus.OK);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.emailValid : ",e);
			
			return ResponseEntity.badRequest().body("회원가입서버에러");
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> dto){
		try {
			ResponseDTO<?> ret = userService.login(dto);
			return new ResponseEntity<>(ret,HttpStatus.OK);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.emailValid : ",e);
			
			return ResponseEntity.badRequest().body("회원가입서버에러");
		}
	}
	
}
