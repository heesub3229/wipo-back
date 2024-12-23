package com.wipo.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wipo.DTO.PostSaveDTO;
import com.wipo.DTO.PostSendDTO;
import com.wipo.DTO.ResponseDTO;
import com.wipo.Entity.FileEntity;
import com.wipo.Entity.FileRelationEntity;
import com.wipo.Entity.FileRelationId;
import com.wipo.Entity.MapEntity;
import com.wipo.Entity.MapRelationEntity;
import com.wipo.Entity.PostEntity;
import com.wipo.Entity.PostRelationEntity;
import com.wipo.Entity.UserEntity;
import com.wipo.Repository.FileRepository;
import com.wipo.Repository.MapRepository;
import com.wipo.Repository.PostRepository;
import com.wipo.Repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PostService {
	
	
	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MapRepository mapRepository;
	
	@Autowired
	private FileService fileService;
	
	@Transactional
	public ResponseDTO<?> setPostSave(PostSaveDTO dto) {
		List<FileEntity> fileArray = null;
		PostEntity postEntity = null;
		List<UserEntity> userArray = null;
		MapEntity mapEntity = null;
		ResponseDTO<?> ret = null;
		try {
			
			UserEntity userEntity = userRepository.findById(dto.getUserSid()).orElse(null);
			
			if(userEntity ==null) {
				throw new Exception("사용자정보에러");
			}
			
			if(dto.getFileArray()==null||dto.getFileArray().size()==0) {
				throw new Exception("사진을 등록하여주세요");
			}
			//파일저장, 파일명, 파일경로 DB저장
			fileArray = fileService.setFileDBSave(dto.getFileArray());
			if(fileArray==null) {
				throw new Exception("사진저장에러");
			}
			
			//맵저장
			if(dto.getMap() ==null) {
				throw new Exception("위치를 선택하여주세요");
			}
			
			mapEntity = mapRepository.findByXAndY(dto.getMap().getX(), dto.getMap().getY());
			if(mapEntity==null) {
				dto.getMap().setCreate_at(ZonedDateTime.now());
				mapEntity = mapRepository.save(dto.getMap());
			}
			
			
			
			//친구정보불러오기
			userArray = new ArrayList<UserEntity>();
			for(Long row : dto.getUserSidArray()) {
				UserEntity freindEntity = userRepository.findById(row).orElse(null);
				if(freindEntity ==null) {
					throw new Exception("친구정보가잘못되었습니다");
				}
				userArray.add(userEntity);
			}
			//게시물저장
			postEntity = PostEntity.builder()
								.content(dto.getContent())
								.create_at(ZonedDateTime.now())
								.create_user_sid(userEntity)
								.date(dto.getDate())
								.update_at(null)
								.build();
			
			postEntity = postRepository.save(postEntity);
			
			//사진 관계저장
			for(FileEntity row:fileArray) {
				FileRelationEntity tempEntity = fileService.relationService.setFileRelationSave(row, postEntity);
				if(tempEntity == null) {
					throw new Exception("사진저장에러");
				}
			}
			//맵-포스트관계저장
			MapRelationEntity tempMapEntity = fileService.relationService.setMapRelationSave(mapEntity, userEntity, postEntity);
			if(tempMapEntity==null) {
				throw new Exception("위치저장에러");
			}
			//포스트 - 친구 관계저장
			for(UserEntity row : userArray) {
				PostRelationEntity tempPostEntity = fileService.relationService.setPostRelationSave(row, postEntity);
				if(tempPostEntity == null) {
					throw new Exception("태그에러");
				}
			}
			List<String> fileBase64Str = new ArrayList<String>();
			for(FileEntity row:fileArray) {
				String fileData = fileService.getImageToBase64(row.getFilepath());
				if(fileData==null) {
					throw new Exception("파일저장에러");
				}
				fileBase64Str.add(fileData);
			}
			
			PostSendDTO res = PostSendDTO.builder()
											.map(mapEntity)
											.post(postEntity)
											.imageArray(fileBase64Str)
											.build();
			
			ret = ResponseDTO.<PostSendDTO>builder()
					.errFlag(false)
					.data(res)
					.resDate(ZonedDateTime.now())
					.build();
		}catch (Exception e) {
			// TODO: handle exception
			if(fileArray!=null) {
				fileService.setFileDelete(fileArray);
			}
			log.error("PostService.setPostSave : {}",e);
			ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
		}
		
		return ret;
	}
	
	public ResponseDTO<?> getMyPost(int page,Long userSid) {
		Page<PostEntity> postPage = null;
		Pageable pageable=null;
		UserEntity userEntity = null;
		List<PostSendDTO> ret = new ArrayList<PostSendDTO>();
		ResponseDTO<?> res = null;
		try {
			
			userEntity = userRepository.findById(userSid).orElse(null);
			if(page==0) {
				
			   
				
				pageable = PageRequest.of(page,5,Sort.by("create_at").descending());
				postPage = postRepository.findByCreateUserSid(userEntity, pageable);
				for(PostEntity row:postPage.getContent()) {
					PostSendDTO dto = PostSendDTO.builder()
													.page(page)
													.post(row)
													.build();
					ret.add(dto);
				}
				pageable = PageRequest.of(page+1,5,Sort.by("create_at").descending());
				postPage = postRepository.findByCreateUserSid(userEntity, pageable);
				for(PostEntity row:postPage.getContent()) {
					PostSendDTO dto = PostSendDTO.builder()
													.page(page+1)
													.post(row)
													.build();
					ret.add(dto);
				}
			}else {
				pageable = PageRequest.of(page,5,Sort.by("create_at").descending());
				postPage = postRepository.findByCreateUserSid(userEntity, pageable);
				for(PostEntity row:postPage.getContent()) {
					PostSendDTO dto = PostSendDTO.builder()
													.page(page)
													.post(row)
													.build();
					ret.add(dto);
				}
			}
			
			if(ret.size()>0) {
				for(PostSendDTO row : ret) {
					PostEntity postEntity = row.getPost();
					List<String> fileStr = fileService.getFileInfo(postEntity.getSid());
					if(fileStr==null) {
						throw new Exception("파일조회에러");
					}
					MapEntity mapEntity = fileService.relationService.getMapRelInfo(userEntity.getSid(), postEntity);
					if(mapEntity==null) {
						throw new Exception("위치조회에러");
					}
					row.setImageArray(fileStr);
					row.setMap(mapEntity);
				}
			}
						
			res = ResponseDTO.<List<PostSendDTO>>builder()
					.errFlag(false)
					.resDate(ZonedDateTime.now())
					.data(ret)
					.build();
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("PostService.getMyPost : {}",e);
			res = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
		}
		return res;
	}

	
	
}
