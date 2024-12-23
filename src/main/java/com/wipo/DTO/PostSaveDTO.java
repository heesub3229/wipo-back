package com.wipo.DTO;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.wipo.Entity.MapEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostSaveDTO {

	private Long userSid;
	private String date;
	private String content;
	private MapEntity map;
	private List<Long> userSidArray;
	private List<MultipartFile> fileArray;
	
}
