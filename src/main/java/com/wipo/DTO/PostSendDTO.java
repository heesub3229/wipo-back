package com.wipo.DTO;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.wipo.Entity.FileEntity;
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
public class PostSendDTO {
	private String type;
	private PostEntity post;
	private MapEntity map;
	private List<FileEntity> files;
}
