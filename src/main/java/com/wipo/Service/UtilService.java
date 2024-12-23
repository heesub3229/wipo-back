package com.wipo.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wipo.DTO.EmailAuthCodeDTO;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UtilService {
	
	private static Map<String, EmailAuthCodeDTO> codes = new ConcurrentHashMap<String, EmailAuthCodeDTO>();
	private static JavaMailSender javaMailSender;
	
	public UtilService(JavaMailSender javaMailSender) {
		UtilService.javaMailSender = javaMailSender;
	}
	
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
	
	public static boolean sendEmail(String email,String subject,String body) {
		
		boolean ret = true;
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper  message = new MimeMessageHelper (mimeMessage,true,"UTF-8");
			message.setTo(email);
			message.setSubject(subject);
			message.setText(body,true);
			message.setFrom("khswnd@gmail.com");
			
			javaMailSender.send(mimeMessage);
			
			ret = false;
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UtilService.sendEmail : {}",e);
			
		}
		return ret;
		
	}
	
	public static boolean validateAuthEmail(String email,String code) {
		boolean ret = true;
		try {
			EmailAuthCodeDTO verificationCode = codes.get(email);
			if (verificationCode != null 
		            && verificationCode.getCode().equals(code) 
		            && verificationCode.getExpiresAt().isAfter(LocalDateTime.now())) {
		            codes.remove(email); // 검증 성공 후 삭제
		            return false;
		        }
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UtilService.validateAuthEmail : {}",e);
		}
		return ret;
	}
	
	public static EmailAuthCodeDTO generateAndSaveCode(String email) {
		EmailAuthCodeDTO ret = null;
		try {
			String code = generateCode();
			ret = new EmailAuthCodeDTO(code, LocalDateTime.now().plusMinutes(3));
	        codes.put(email, ret);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UtilService.generateAndSaveCode : {}",e);
		}
        
        return ret;
    }
	
	private static String generateCode() {
		
		String ret = "";
		try {
			Random random = new Random();
			int code = 1000 + random.nextInt(9000);
			ret = String.valueOf(code);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UtilService.generateCode : {}",e);
		}
		
		return ret;
	}
	
	@Scheduled(cron = "0 0 * * * ?")
	public void removeExpiredCodes() {
		try {
			codes.entrySet().removeIf(entry->entry.getValue().getExpiresAt().isBefore(LocalDateTime.now()));
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UtilService.removeExpiredCodes : {}",e);
		}
	}
	
	
	public static String getFilenameToDate(String filename) {
		String ret = "";
		
		try {
			int dotIndex = filename.lastIndexOf('.');
			String namePart = (dotIndex != -1) ? filename.substring(0, dotIndex) : filename;
	        String extensionPart = (dotIndex != -1) ? filename.substring(dotIndex) : "";

	        // 현재 시간 가져오기 (분초 이하 마이크로초까지)
	        ZonedDateTime now = ZonedDateTime.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mmssSSS");

	        // 새로운 파일명 생성
	        ret = namePart+"_"+now.format(formatter)+extensionPart;
	        
		}catch (Exception e) {
			// TODO: handle exception
			log.error("UtilService.getFileExtenstion : {}",e);
			ret = "";
		}
		return ret;
	}
}
