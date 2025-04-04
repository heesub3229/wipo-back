package com.wipo.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wipo.DTO.AlertSendDTO;
import com.wipo.DTO.ResponseDTO;
import com.wipo.DTO.RestSaveDTO;
import com.wipo.DTO.RestSendDTO;
import com.wipo.Entity.FileEntity;
import com.wipo.Entity.MapEntity;
import com.wipo.Entity.RestEntity;
import com.wipo.Entity.RestRelationEntity;
import com.wipo.Entity.UserEntity;
import com.wipo.Repository.MapRepository;
import com.wipo.Repository.RestRepository;
import com.wipo.Repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RestService {
	
	@Autowired
	private RestRepository restRepository;
	
	@Autowired
	private MapRepository mapRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private RelationService relService;
	
	@Transactional
	public ResponseDTO<?> setRestSave(RestSaveDTO dto,Long userSid){
		ResponseDTO<?> ret = null;
		FileEntity file = null;
		MapEntity map = null;
		RestEntity rest = null;
		try {
			UserEntity userEntity = userRepository.findById(userSid).orElse(null);
			if(userEntity==null) {
				throw new Exception("유저정보에러");
			}
			if(dto.getImage() != null) {
				file = fileService.setFileDBSaveOne(dto.getImage());
			}
			if(dto.getMap().getX()==0.0&&dto.getMap().getY()==0.0) {
				throw new Exception("맵정보에러");
			}
			map = mapRepository.findByXAndY(dto.getMap().getX(), dto.getMap().getY());
			if(map==null) {
				dto.getMap().setCreate_at(ZonedDateTime.now());
				map = mapRepository.save(dto.getMap());
			}
			
			if(dto.getRestaurant().getSid()==null) {
				dto.getRestaurant().setMap(map);
				dto.getRestaurant().setFile(file);
				dto.getRestaurant().setCreate_user_sid(userEntity);
				dto.getRestaurant().setCreate_at(ZonedDateTime.now());
				rest = restRepository.save(dto.getRestaurant());
			}else {
				rest = restRepository.findById(dto.getRestaurant().getSid()).orElse(null);
				if(rest == null) {
					throw new Exception("맛집정보에러");
				}
				rest.setCategory(dto.getRestaurant().getCategory());
				if(dto.getImage() != null) {
					rest.setFile(file);
				}
				rest.setMap(map);
				rest.setMemo(dto.getRestaurant().getMemo());
				rest.setMenuName(dto.getRestaurant().getMenuName());
				rest.setPlaceName(dto.getRestaurant().getPlaceName());
				
				rest = restRepository.save(rest);
			}
			
			ret = ResponseDTO.<RestEntity>builder()
					.errFlag(false)
					.data(rest)
					.resDate(ZonedDateTime.now())
					.build();
			
			
		}catch (Exception e) {
			// TODO: handle exception
			if(file != null) {
				fileService.setFileDeleteOne(file);
			}
			log.error("RestService.setRestSave : {}",e);
			ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
		}
		return ret;
	}

	public ResponseDTO<?> setRestApprove(Long userSid,Long restRelSid){
		ResponseDTO<?> ret = null;
		try {
			UserEntity userEntity = userRepository.findById(userSid).orElse(null);
			if(userEntity==null) {
				throw new Exception("재로그인요청");
			}
			
			RestRelationEntity relEntity = relService.setRestRelApprove(restRelSid);
			if(relEntity==null) {
				throw new Exception("알람에러");
			}
			
			RestEntity restEntity = relEntity.getRest();
			restEntity.setSid(null);
			restEntity.setCreate_user_sid(userEntity);
			restEntity = restRepository.save(restEntity);
			
			ret = ResponseDTO.<RestEntity>builder()
					.errFlag(false)
					.data(restEntity)
					.resDate(ZonedDateTime.now())
					.build();
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RestService.setRestApprove : {}",e);
			ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
		}
		return ret;
	}

	public ResponseDTO<?> getRestList(Long userSid){
		ResponseDTO<?> ret = null;
		try {
			
			UserEntity userEntity = userRepository.findById(userSid).orElse(null);
			if(userEntity == null) {
				throw new Exception("유저정보 조회에러");
			}
			
			List<RestEntity> restArray = restRepository.getRestUserToList(userEntity);
			
			List<RestSendDTO> retArray = new ArrayList<RestSendDTO>();
			
			for(RestEntity row: restArray) {
				RestSendDTO tempDto = new RestSendDTO();
				int count = relService.findByMapAndUserCount(userEntity, row.getMap());
				tempDto.setCreate_at(row.getCreate_at());
				tempDto.setCreate_user_sid(row.getCreate_user_sid());
				tempDto.setFile(row.getFile());
				tempDto.setMap(row.getMap());
				tempDto.setMemo(row.getMemo());
				tempDto.setMenuName(row.getMenuName());
				tempDto.setPlaceName(row.getPlaceName());
				tempDto.setPostCount(count);
				tempDto.setRating(row.getRating());
				tempDto.setSid(row.getSid());
				retArray.add(tempDto);
			}
			
			ret = ResponseDTO.builder()
					.errFlag(false)
					.data(retArray)
					.build();
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RestService.getRestList : {}",e);
			ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
		}
		return ret;
	}

}
