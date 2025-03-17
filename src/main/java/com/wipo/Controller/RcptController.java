package com.wipo.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.wipo.Appconfig.JwtTokenProvider;
import com.wipo.DTO.JwtDTO;
import com.wipo.DTO.ResponseDTO;
import com.wipo.Entity.RcptEntity;
import com.wipo.Service.RcptService;
import com.wipo.Service.UtilService;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("rcpt")
public class RcptController {
	
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private RcptService rcptService;
	
	@PostMapping("/setRcptSave")
	public ResponseEntity<?> setRcptSave(@RequestHeader("Authorization") String jwt, @RequestBody RcptEntity rcptEntity){
		try {
			if(!jwtTokenProvider.validateJwt(jwt)) {
				Claims claims = jwtTokenProvider.validateToken(jwt);
				String json = claims.get("user",String.class);
				
				JwtDTO jwtDto = UtilService.parseJsonToDto(json, JwtDTO.class);
				ResponseDTO<?> ret = rcptService.setRcptSave(jwtDto.getSid(), rcptEntity);
				
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
			log.error("RcptController.setRcptSave : {} ",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
	}
	
	@GetMapping("/getRcpInfo")
	private ResponseEntity<?> getRcpInfo(@RequestHeader("Authorization") String jwt ,@RequestParam("page") int page){
		try {
			if(!jwtTokenProvider.validateJwt(jwt)) {
				Claims claims = jwtTokenProvider.validateToken(jwt);
				String json = claims.get("user",String.class);
				
				JwtDTO jwtDto = UtilService.parseJsonToDto(json, JwtDTO.class);
				ResponseDTO<?> ret = rcptService.getRcpInfo(jwtDto.getSid(), page);
				
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
			log.error("RcptController.setRcptSave : {} ",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
	}
	
	@GetMapping("/getRcptGraphMonth")
	private ResponseEntity<?> getRcptGraphMonth(@RequestHeader("Authorization") String jwt ,@RequestParam("page") int page){
		try {
			if(!jwtTokenProvider.validateJwt(jwt)) {
				Claims claims = jwtTokenProvider.validateToken(jwt);
				String json = claims.get("user",String.class);
				
				JwtDTO jwtDto = UtilService.parseJsonToDto(json, JwtDTO.class);
				ResponseDTO<?> ret = rcptService.getRcptGraphMonth(jwtDto.getSid(), page);
				
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
			log.error("RcptController.getRcptGraphMonth : {} ",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
	}
	
	@GetMapping("/getRcptGraphDay")
	private ResponseEntity<?> getRcptGraphDay(@RequestHeader("Authorization") String jwt ,@RequestParam("page") int page){
		try {
			if(!jwtTokenProvider.validateJwt(jwt)) {
				Claims claims = jwtTokenProvider.validateToken(jwt);
				String json = claims.get("user",String.class);
				
				JwtDTO jwtDto = UtilService.parseJsonToDto(json, JwtDTO.class);
				ResponseDTO<?> ret = rcptService.getRcptGraphDay(jwtDto.getSid(), page);
				
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
			log.error("RcptController.getRcptGraphDay : {} ",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
	}
	
}
