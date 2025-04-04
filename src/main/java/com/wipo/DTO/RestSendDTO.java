package com.wipo.DTO;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import com.wipo.Entity.FileEntity;
import com.wipo.Entity.MapEntity;
import com.wipo.Entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestSendDTO {
	
	private Long sid;
	private String placeName;
	private String menuName;
	private String memo;
	private BigDecimal rating;
	private FileEntity file;
	private MapEntity map;
	private ZonedDateTime create_at;
	private UserEntity create_user_sid;
	private int postCount;

}
