package com.wipo.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NaverInfoDTO {
	private String resultcode;
	private String message;
	private Response response;
	
	@Data
	public static class Response{
		private String id;
		private String name;
		private String email;
		private String birthday;
		private String birthyear;
	}
}
