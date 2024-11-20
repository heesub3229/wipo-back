package com.wipo.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleTokenDTO {
	private String access_token;
	private String refresh_token;
	private Long expires_in;
	private String scope;
	private String id_token;
}
