package com.wipo.DTO;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wipo.DTO.KakaoUserDTO.KakaoAccount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NaverTokenDTO {
	private String access_token;
	private String refresh_token;
	private String token_type;
	private Long expires_in;
}
