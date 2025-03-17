package com.wipo.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wipo.DTO.ResponseDTO;
import com.wipo.Entity.CheckEntity;
import com.wipo.Entity.UserEntity;
import com.wipo.Repository.CheckRepository;
import com.wipo.Repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CheckService {

	
	@Autowired
	private CheckRepository checkRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	public ResponseDTO<?> setCheckSave(Long userSid,CheckEntity checkEntity){
		ResponseDTO<?> ret = null;
		CheckEntity res = null;
		try {
			UserEntity userEntity = userRepository.findById(userSid).orElse(null);
			if(userEntity==null) {
				throw new Exception("재로그인요청");
			}
			if(checkEntity.getSid()==null) {
				res = CheckEntity.builder()
						.checkFlag(checkEntity.getCheckFlag())
						.create_at(ZonedDateTime.now())
						.create_user_sid(userEntity)
						.title(checkEntity.getTitle())
						.object(checkEntity.getObject())
						.build();
				res = checkRepository.save(res);
			}else {
				res = checkRepository.save(res);
			}
			ret = ResponseDTO.<CheckEntity>builder()
					.data(res)
					.errFlag(false)
					.resDate(ZonedDateTime.now())
					.build();
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RcptService.setRcptSave : {}",e);
			ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
		}
		return ret;
	}
	
	public ResponseDTO<?> getCheckList(Long userSid){
		ResponseDTO<?> ret = null;
		try {
			UserEntity userEntity = userRepository.findById(userSid).orElse(null);
			if(userEntity == null) {
				throw new Exception("재로그인 요청");
			}
			List<CheckEntity> checkArray = checkRepository.findByCreateUserSid(userEntity);
			
			ret = ResponseDTO.<List<CheckEntity>>builder()
					.errFlag(false)
					.data(checkArray)
					.resDate(ZonedDateTime.now())
					.build();
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RcptService.getCheckList : {}",e);
			ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
		}
		return ret;
	}

	@Transactional
	public ResponseDTO<?> delCheckList(List<Long> checkArray){
		ResponseDTO<?> ret = null;
		try {
			for(Long row:checkArray) {
				CheckEntity checkEntity = checkRepository.findById(row).orElse(null);
				if(checkEntity == null) {
					throw new Exception("체크리스트에러");
				}
				checkRepository.delete(checkEntity);
			}
			
			
			ret = ResponseDTO.<List<CheckEntity>>builder()
					.errFlag(false)
					.resDate(ZonedDateTime.now())
					.build();
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RcptService.getCheckList : {}",e);
			ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
		}
		return ret;
	}

}
