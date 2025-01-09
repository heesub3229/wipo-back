package com.wipo.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wipo.DTO.PostSaveDTO;
import com.wipo.DTO.PostSendDTO;
import com.wipo.DTO.ResponseDTO;
import com.wipo.Entity.FileEntity;
import com.wipo.Entity.FileRelationEntity;
import com.wipo.Entity.MapEntity;
import com.wipo.Entity.MapRelationEntity;
import com.wipo.Entity.PostEntity;
import com.wipo.Entity.PostRelationEntity;
import com.wipo.Entity.UserEntity;
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
	
	private final RelationService relationService;
	
	public PostService(RelationService relationService) {
		this.relationService = relationService;
	}
	
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
				userArray.add(freindEntity);
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
				FileRelationEntity tempEntity = relationService.setFileRelationSave(row, postEntity);
				if(tempEntity == null) {
					throw new Exception("사진저장에러");
				}
			}
			//맵-포스트관계저장
			MapRelationEntity tempMapEntity = relationService.setMapRelationSave(mapEntity, userEntity, postEntity);
			if(tempMapEntity==null) {
				throw new Exception("위치저장에러");
			}
			//포스트 - 친구 관계저장
			for(UserEntity row : userArray) {
				PostRelationEntity tempPostEntity = relationService.setPostRelationSave(row, postEntity,"N");
				if(tempPostEntity == null) {
					throw new Exception("태그에러");
				}
			}
			PostSendDTO res = PostSendDTO.builder()
					.type("F")
					.map(mapEntity)
					.post(postEntity)
					.files(fileArray)
					.build();
			for(UserEntity row : userArray) {
				SseEmitter client = AlertService.validClient(row.getSid());
				if(client!= null) {
					ObjectMapper mapper = new ObjectMapper();
					String json = mapper.writeValueAsString(res);
					client.send(SseEmitter.event().name("postFriend").data(json));
				}
			}
			res.setType("U");
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
			//내가만든 게시물
			userEntity = userRepository.findById(userSid).orElse(null);
			
			pageable = PageRequest.of(page,10,Sort.by("create_at").descending());
			postPage = postRepository.findByCreateUserSid(userEntity, pageable);
			for(PostEntity row:postPage.getContent()) {
				PostSendDTO dto = PostSendDTO.builder()
												.post(row)
												.type("U")
												.build();
				ret.add(dto);
			}
			
			
			if(ret.size()>0) {
				for(PostSendDTO row : ret) {
					PostEntity postEntity = row.getPost();
					List<FileEntity> files = fileService.getFileInfo(postEntity);
					if(files==null) {
						throw new Exception("파일조회에러");
					}
					MapEntity mapEntity = relationService.getMapRelInfo(userEntity, postEntity);
					if(mapEntity==null) {
						throw new Exception("위치조회에러");
					}
					row.setFiles(files);
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

	public ResponseDTO<?> getOtherPost(int page, Long userSid){
		List<PostEntity> postPage = null;
		UserEntity userEntity = null;
		List<PostSendDTO> ret = new ArrayList<PostSendDTO>();
		ResponseDTO<?> res = null;
		try {
			userEntity = userRepository.findById(userSid).orElse(null);
			
			postPage = relationService.getPostRelInfo(userEntity, page);
			
			for(PostEntity row:postPage) {
				MapEntity mapEntity = relationService.getMapRelInfo(row.getCreate_user_sid(), row);
				List<FileEntity> files = fileService.getFileInfo(row);
				
				PostSendDTO dto = PostSendDTO.builder()
						.type("F")
						.post(row)
						.map(mapEntity)
						.files(files)
						.build();
				ret.add(dto);
			}
			
			res = ResponseDTO.<List<PostSendDTO>>builder()
					.errFlag(false)
					.resDate(ZonedDateTime.now())
					.data(ret)
					.build();
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("PostService.getOtherPost : {}",e);
			res = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
		}
		return res;
	}
	
	public ResponseDTO<?> getPostInfo(Long userSid,Long postSid){
		ResponseDTO<?> res = null;
		try {
			UserEntity userEntity = userRepository.findById(userSid).orElse(null);
			if(userEntity==null) {
				throw new Exception("유저에러");
			}
			
			PostEntity postEntity = postRepository.findById(postSid).orElse(null);
			if(postEntity==null) {
				throw new Exception("위치저장에러");
			}
			
			List<FileEntity> files = fileService.getFileInfo(postEntity);
			if(files==null) {
				throw new Exception("파일조회에러");
			}
			MapEntity mapEntity = relationService.getMapRelInfo(postEntity.getCreate_user_sid(), postEntity);
			if(mapEntity==null) {
				throw new Exception("위치조회에러");
			}
			
			PostRelationEntity tempPostEntity = relationService.setPostRelationSave(userEntity, postEntity,"Y");
			if(tempPostEntity == null) {
				throw new Exception("포스팅에러");
			}
			PostSendDTO dto = PostSendDTO.builder()
											.post(postEntity)
											.map(mapEntity)
											.files(files)
											.build();
			res = ResponseDTO.<PostSendDTO>builder()
										.errFlag(false)
										.resDate(ZonedDateTime.now())
										.data(dto)
										.build();
		}catch (Exception e) {
			// TODO: handle exception
			log.error("PostService.getPostInfo : {}",e);
			res = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
		}
		return res;
	}
	
	
}
