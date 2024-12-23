package com.wipo.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wipo.Entity.FileEntity;
import com.wipo.Entity.FileRelationEntity;
import com.wipo.Entity.FileRelationId;
import com.wipo.Entity.MapEntity;
import com.wipo.Entity.MapRelationEntity;
import com.wipo.Entity.MapRelationId;
import com.wipo.Entity.PostEntity;
import com.wipo.Entity.PostRelationEntity;
import com.wipo.Entity.PostRelationId;
import com.wipo.Entity.UserEntity;
import com.wipo.Entity.UserRelationEntity;
import com.wipo.Entity.UserRelationId;
import com.wipo.Repository.MapRelationRepository;
import com.wipo.Repository.PostRelationRepository;
import com.wipo.Repository.UserRelationRepository;
import com.wipo.Repository.fileRelationRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RelationService {

	@Autowired
	private fileRelationRepository fileRelationRepository;
	
	@Autowired
	private MapRelationRepository mapRelationRepository;
	
	@Autowired
	private UserRelationRepository userRelationRepository;
	
	@Autowired
	private PostRelationRepository postRelationRepository;
	
	public FileRelationEntity setFileRelationSave(FileEntity fileEntity,PostEntity postEntity){
		FileRelationEntity ret = new FileRelationEntity();
		try {
			FileRelationId relId = FileRelationId.builder()
											.file_sid(fileEntity.getSid())
											.post_sid(postEntity.getSid())
											.build();
			
			ret = FileRelationEntity.builder()
					.id(relId)
					.create_at(ZonedDateTime.now())
					.file(fileEntity)
					.post(postEntity)
					.build();
			ret = fileRelationRepository.save(ret);
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RelationService.setFileRelationSave : {}",e);
			ret = null;
		}
		return ret;
	}
	
	public MapRelationEntity setMapRelationSave(MapEntity mapEntity,UserEntity userEntity,PostEntity postEntity) {
		MapRelationEntity ret = new MapRelationEntity();
		try {
			MapRelationId relId = MapRelationId.builder()
											.map_sid(mapEntity.getSid()).user_sid(userEntity.getSid())
											.build();
			
			ret = MapRelationEntity.builder()
					.id(relId)
					.create_at(ZonedDateTime.now())
					.post(postEntity)
					.build();
			ret = mapRelationRepository.save(ret);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RelationService.setMapRelationSave : {}",e);
			ret = null;
		}
		
		return ret;
	}
	
	
	public PostRelationEntity setPostRelationSave(UserEntity userEntity,PostEntity postEntity) {
		PostRelationEntity ret = new PostRelationEntity();
		try {
			PostRelationId relId = PostRelationId.builder()
											.post(postEntity)
											.user(userEntity)
											.build();
			
			ret = PostRelationEntity.builder()
					.id(relId)
					.create_at(ZonedDateTime.now())
					.build();
			ret = postRelationRepository.save(ret);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RelationService.setPostRelationSave : {}",e);
			ret = null;
		}
		
		return ret;
	}
	
	
	public UserRelationEntity setUserRelationSave(UserEntity userEntity,UserEntity friendEntity) {
		UserRelationEntity ret = new UserRelationEntity();
		try {
			UserRelationId relId = UserRelationId.builder()
											.friend(friendEntity)
											.user(userEntity)
											.build();
			
			ret = UserRelationEntity.builder()
					.create_at(ZonedDateTime.now())
					.id(relId)
					.build();
			
			ret = userRelationRepository.save(ret);
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RelationService.setUserRelationSave : {}",e);
			ret = null;
		}
		return ret;
	}

	public List<FileRelationEntity> getFileRelationInfo(Long postSid) {
		List<FileRelationEntity> ret = null;
		try {
			ret = fileRelationRepository.findByPostSid(postSid);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RelationService.getFileRelationInfo : {}",e);
			ret = null;
		}
		return ret;
	}
	
	public MapEntity getMapRelInfo(Long userSid,PostEntity postEntity) {
		MapEntity ret = null;
		try {
			MapRelationEntity relEntity = mapRelationRepository.findByPostANDUser(userSid, postEntity);
			ret = relEntity.getMap();
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RelationService.getMapRelInfo : {}",e);
			ret = null;
		}
		return ret;
	}

	public List<UserRelationEntity> getUserRelInfo(UserEntity userEntity){
		List<UserRelationEntity> ret = null;
		try {
			ret = userRelationRepository.findByIdAndUser(userEntity);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RelationService.getUserRelInfo : {}",e);
			ret = null;
		}
		return ret;
	}
	
	
}
