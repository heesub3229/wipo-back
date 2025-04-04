package com.wipo.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.wipo.Appconfig.JwtTokenProvider;
import com.wipo.DTO.JwtDTO;
import com.wipo.DTO.ResponseDTO;
import com.wipo.DTO.RestSaveDTO;
import com.wipo.Service.RestService;
import com.wipo.Service.UtilService;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("rest")
@org.springframework.web.bind.annotation.RestController
public class RestController {

	@Autowired
	private RestService restService;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@PostMapping(value="/restSave",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	private ResponseEntity<?> restSave(@RequestHeader("Authorization")String jwt,
														@RequestPart(value = "data",required = false)RestSaveDTO dto,
														@RequestPart(value = "file",required = false)MultipartFile file){
		
		try {
			if(!jwtTokenProvider.validateJwt(jwt)) {
				dto.setImage(file);
				Claims claims = jwtTokenProvider.validateToken(jwt);
				String json = claims.get("user",String.class);
				
				JwtDTO jwtDto = UtilService.parseJsonToDto(json, JwtDTO.class);
				ResponseDTO<?> ret = restService.setRestSave(dto, jwtDto.getSid());
				
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
			log.error("RestController.restSave : {} ",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
		
	}
	
	@GetMapping("/getRestList")
	private ResponseEntity<?> getRestList(@RequestHeader("Authorization")String jwt){
		try {
			if(!jwtTokenProvider.validateJwt(jwt)) {
				Claims claims = jwtTokenProvider.validateToken(jwt);
				String json = claims.get("user",String.class);
				
				JwtDTO jwtDto = UtilService.parseJsonToDto(json, JwtDTO.class);
				ResponseDTO<?> ret = restService.getRestList(jwtDto.getSid());
				
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
			log.error("RestController.getRestList : {} ",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
	}
	
}
