package com.wipo.DTO;

import java.util.List;

import com.wipo.Entity.FileEntity;
import com.wipo.Entity.MapEntity;
import com.wipo.Entity.PostEntity;
import com.wipo.Entity.RcptEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RcptSendDTO {
	private Long totalIAmount;
	private Long totalEAmount;
	private List<RcptEntity> rcptArray;
}
