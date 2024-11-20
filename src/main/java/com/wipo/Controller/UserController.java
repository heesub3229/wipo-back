package com.wipo.Controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wipo.DTO.ResponseDTO;
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
	
	//네이버 로그인
	@GetMapping("/naverLogin")
	public ResponseEntity<?> naverLogin(@RequestParam("code")String code){
		try {
			ResponseDTO<String> ret = userService.naverLogin(code);
			return new ResponseEntity<>(ret,HttpStatus.OK); 
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UserController.naverLogin : ",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
		
	}
}
