package com.wipo.DTO;

import java.util.List;

import com.wipo.Entity.MapEntity;
import com.wipo.Entity.PostEntity;
import com.wipo.Entity.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendUserInfoDTO {
	private UserEntity user;
	private List<UserEntity> friend;
	private List<AlertSendDTO> alert;
}
