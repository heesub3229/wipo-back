package com.wipo.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class JwtDTO {

	private Long sid;
	private String type;
	private String access_token;
	private String id_token;
	private Long expires_in;
	private String refresh_token;
	private Long refresh_token_expires_in;
}
