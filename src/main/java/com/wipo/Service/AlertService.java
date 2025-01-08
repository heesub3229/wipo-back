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
			
		}catch (Exception e) {
			log.error("AlertService.getAlertArray : {}",e);
			// TODO: handle exception
			ret = null;
		}
		return ret;
	}

	
}
