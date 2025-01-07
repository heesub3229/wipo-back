package com.wipo.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertSendDTO {

	private Long sid;
	private String type; //P 포스팅 F 친구추가 A 공지 FA 친구수락 FR 친구거절
	private String title;
	private String content;
	private String date;
	private String confirm_flag;
	private String approve_flag;
	
}
