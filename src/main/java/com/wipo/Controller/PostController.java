package com.wipo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wipo.Appconfig.JwtTokenProvider;
import com.wipo.DTO.JwtDTO;
import com.wipo.DTO.PostSaveDTO;
import com.wipo.DTO.ResponseDTO;
import com.wipo.Service.PostService;
import com.wipo.Service.UtilService;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("post")
@RestController
public class PostController {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private PostService postService;
	
	@PostMapping(value="/postSave",consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	private ResponseEntity<?> postSave(@RequestHeader("Authorization") String jwt ,
															@RequestPart("data") PostSaveDTO dto,
															@RequestPart("files")List<MultipartFile> files){
		try {
			if(!jwtTokenProvider.validateJwt(jwt)) {
				Claims claims = jwtTokenProvider.validateToken(jwt);
				String json = claims.get("user",String.class);
				
				JwtDTO jwtDto = UtilService.parseJsonToDto(json, JwtDTO.class);
				dto.setUserSid(jwtDto.getSid());
				dto.setFileArray(files);
				ResponseDTO<?> ret = postService.setPostSave(dto);
				
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
			log.error("PostController.postSave : {} ",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
	}
	
	@GetMapping("/getPostMy")
	private ResponseEntity<?> getPostMy(@RequestHeader("Authorization") String jwt ,@RequestParam("page") int page){
		try {
			if(!jwtTokenProvider.validateJwt(jwt)) {
				Claims claims = jwtTokenProvider.validateToken(jwt);
				String json = claims.get("user",String.class);
				
				JwtDTO jwtDto = UtilService.parseJsonToDto(json, JwtDTO.class);
				ResponseDTO<?> ret = postService.getMyPost(page, jwtDto.getSid());
				
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
			log.error("PostController.getPostMy : {} ",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
	}

}
