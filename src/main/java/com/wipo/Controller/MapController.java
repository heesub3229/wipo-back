package com.wipo.Controller;

import java.util.List;

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
import com.wipo.DTO.FavSaveDTO;
import com.wipo.DTO.JwtDTO;
import com.wipo.DTO.ResponseDTO;
import com.wipo.Entity.MapEntity;
import com.wipo.Service.MapService;
import com.wipo.Service.UtilService;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("map")
@RestController
public class MapController {
	
	@Autowired
	private MapService mapService;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@PostMapping("/setFavMap")
	private ResponseEntity<?> setFavMap(@RequestHeader("Authorization") String jwt ,@RequestBody List<FavSaveDTO> favArray){
		try {
			if(!jwtTokenProvider.validateJwt(jwt)) {
				Claims claims = jwtTokenProvider.validateToken(jwt);
				String json = claims.get("user",String.class);
				
				JwtDTO jwtDto = UtilService.parseJsonToDto(json, JwtDTO.class);
				ResponseDTO<?> ret = mapService.setFavMap(jwtDto.getSid(), favArray);
				
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
			log.error("MapController.setFavMap : {} ",e);
			
			return ResponseEntity.badRequest().body("로그인서버에러");
		}
	}

}
