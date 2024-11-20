package com.wipo.Service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UtilService {
	
	
	public static <T> T parseJsonToDto(String json,Class<T> dto){
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return mapper.readValue(json, dto);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UtilService.parseJsonToDto : {}",e);
			return null;
		}
		
	}
	
	public static String generateState() {
		String ret = "";
		try {
			ret = UUID.randomUUID().toString();
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UtilService.generateState : {}",e);
		}
		return ret;
	}
	
}
