package com.wipo.DTO;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.wipo.Entity.MapEntity;
import com.wipo.Entity.RestEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestSaveDTO {
	private RestEntity restaurant;
	private List<Long> friend;
	private MultipartFile image;
	private MapEntity map;
}
