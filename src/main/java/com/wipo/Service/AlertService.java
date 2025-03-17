package com.wipo.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wipo.DTO.AlertSendDTO;
import com.wipo.Entity.PostEntity;
import com.wipo.Entity.PostRelationEntity;
import com.wipo.Entity.RestRelationEntity;
import com.wipo.Entity.UserEntity;
import com.wipo.Entity.UserRelationEntity;
import com.wipo.Repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AlertService {
	
	@Autowired
	private RelationService relationService;
	
	@Autowired
	private UserRepository userRepository;

	private static ConcurrentHashMap<Long, SseEmitter> clients = new ConcurrentHashMap<Long, SseEmitter>();
		
	public SseEmitter setClients(Long userSid) {
		SseEmitter ret = null;
		try {
			log.info(userSid.toString()+"자동로그인");
			ret = new SseEmitter(0L);
			clients.put(userSid, ret);
			ret.onTimeout(() -> clients.remove(userSid));
			ret.onError((e) -> clients.remove(userSid));
			List<AlertSendDTO> alertArray = getAlertArray(userSid);
			if(alertArray!=null) {
				for(AlertSendDTO row : alertArray) {
					String wtRet= writeMessage(userSid,row);
					if(wtRet==null) {
						break;
					}
				}			
			}
			
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("AlertService.setUserRelationSave : {}",e);
			ret = null;
		}
		return ret;
	}
	
	public void disconStream(Long userSid) {
		try {
			log.info(userSid.toString()+"로그인종료");
			clients.remove(userSid);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("AlertService.disconStream : {}",e);
		}
	}
	
	public String writeMessage(Long userSid,AlertSendDTO dto) {
		String ret = "";
		try {
			ObjectMapper mapper = new ObjectMapper();
			
			ret = mapper.writeValueAsString(dto);
			
			SseEmitter clientResponse = clients.get(userSid);
			if(clientResponse!= null) {
				clientResponse.send(SseEmitter.event().name("alert").data(ret));
			}else {
				ret = null;
			}
			
		}catch (Exception e) {
			log.error("AlertService.writeMessage : {}",e);
			// TODO: handle exception
			ret = null;
		}
		return ret;
	}
	
	public static SseEmitter validClient(Long userSid) {
		SseEmitter ret = null;
		try {
			SseEmitter clienResponse = clients.get(userSid);
			if(clienResponse !=null) {
				ret = clienResponse;
			}else {
				ret = null;
			}
			
		}catch (Exception e) {
			log.error("AlertService.validClient : {}",e);
			// TODO: handle exception
			ret = null;
		}
		return ret;
	}
	
	private List<AlertSendDTO> getAlertArray(Long userSid){
		List<AlertSendDTO> ret = new ArrayList<AlertSendDTO>();
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd");
			UserEntity userEntity = userRepository.findById(userSid).orElse(null);
			List<UserRelationEntity> userRelArray = relationService.getFriendRelWait(userEntity);
			List<UserRelationEntity> yesOrNoArray = relationService.getRelApproveYesOrNo(userEntity);
			List<PostRelationEntity> postArray = relationService.getPostRelToAlert(userEntity);
			List<RestRelationEntity> restArray = relationService.getRestRelInfo(userEntity);
			//친구요청알림
			for(UserRelationEntity row : userRelArray) {
				AlertSendDTO dto = AlertSendDTO.builder()
												.content(row.getUser().getName()+"님이 친구 요청을 보냈습니다.")
												.date(row.getCreate_at().format(formatter))
												.title(row.getUser().getEmail())
												.sid(row.getSid())
												.confirm_flag(row.getConfirm_flag())
												.approve_flag(row.getApprove_flag())
												.type("F")
												.build();
				ret.add(dto);
			}
			//친구 요청 수락, 거절알림
			String str = "";
			for(UserRelationEntity row:yesOrNoArray) {
				if(row.getApprove_flag().equals("Y")) {
					str = "님이 친구 요청을 수락했습니다.";
				}else {
					str = "님이 친구 요청을 거절했습니다.";
				}
				AlertSendDTO dto = AlertSendDTO.builder()
											.approve_flag(row.getApprove_flag())
											.confirm_flag(row.getConfirm_flag())
											.content(row.getFriend().getName()+str)
											.date(row.getUpdate_at().format(formatter))
											.sid(row.getSid())
											.title(row.getFriend().getEmail())
											.type("F")
											.build();
				ret.add(dto);
				
			}
			//포스팅 알림
			for(PostRelationEntity row: postArray) {
				AlertSendDTO dto = AlertSendDTO.builder()
												.confirm_flag(row.getConfirm_flag())
												.content(row.getPost().getCreate_user_sid().getName()+"님이 게시물을 포스팅했습니다.")
												.date(row.getCreate_at().format(formatter))
												.sid(row.getPost().getSid())
												.title(row.getPost().getCreate_user_sid().getEmail())
												.type("P")
												.build();
				ret.add(dto);
			}
			
			//맛집추천알림
			if(restArray!=null&&restArray.size()>0) {
				for(RestRelationEntity row:restArray) {
					AlertSendDTO dto = AlertSendDTO.builder()
												.content(row.getUser().getName()+"님이 맛집을 추천했습니다.")
												.title(row.getUser().getEmail())
												.sid(row.getSid())
												.confirm_flag(row.getConfirm_flag())
												.date(row.getCreate_at().format(formatter))
												.type("R")
												.build();
					ret.add(dto);
				}
			}
		}catch (Exception e) {
			log.error("AlertService.getAlertArray : {}",e);
			// TODO: handle exception
			ret = null;
		}
		return ret;
	}

	
}
