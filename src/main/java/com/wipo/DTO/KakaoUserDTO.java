package com.wipo.DTO;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class KakaoUserDTO {
	private Long id;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
	private Date connected_at;
	private KakaoAccount kakao_account;
	
	@Data
	public static class KakaoAccount{
		private String email;
		private Profile profile;
		
		
	}
	@Data
	public static class Profile{
		private String nickname;
	}
	
}