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

import com.wipo.DTO.FavSaveDTO;
import com.wipo.Entity.FileEntity;
import com.wipo.Entity.FileRelationEntity;
import com.wipo.Entity.MapEntity;
import com.wipo.Entity.MapRelationEntity;
import com.wipo.Entity.PostEntity;
import com.wipo.Entity.PostRelationEntity;
import com.wipo.Entity.RestEntity;
import com.wipo.Entity.RestRelationEntity;
import com.wipo.Entity.UserEntity;
import com.wipo.Entity.UserRelationEntity;
import com.wipo.Repository.MapRelationRepository;
import com.wipo.Repository.PostRelationRepository;
import com.wipo.Repository.RestRelationRepository;
import com.wipo.Repository.UserRelationRepository;
import com.wipo.Repository.UserRepository;
import com.wipo.Repository.fileRelationRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RelationService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private fileRelationRepository fileRelationRepository;
	
	@Autowired
	private MapRelationRepository mapRelationRepository;
	
	@Autowired
	private UserRelationRepository userRelationRepository;
	
	@Autowired
	private PostRelationRepository postRelationRepository;
	
	@Autowired
	private RestRelationRepository restRelationRepository;
		
	public FileRelationEntity setFileRelationSave(FileEntity fileEntity,PostEntity postEntity){
		FileRelationEntity ret = null;
		try {
			
			ret = fileRelationRepository.findByPostAndFile(postEntity, fileEntity);
			
			if(ret == null) {
				ret = FileRelationEntity.builder()
						.create_at(ZonedDateTime.now())
						.file(fileEntity)
						.post(postEntity)
						.build();
			}else {
				ret.setFile(fileEntity);
				ret.setPost(postEntity);
			}
			
			
			ret = fileRelationRepository.save(ret);
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RelationService.setFileRelationSave : {}",e);
			ret = null;
		}
		return ret;
	}
	
	public MapRelationEntity setMapRelationSave(MapEntity mapEntity,UserEntity userEntity,PostEntity postEntity) {
		MapRelationEntity ret = null;
		try {
			ret = mapRelationRepository.findByPostANDUser(userEntity, postEntity);
			if(ret == null) {
				ret = MapRelationEntity.builder()
						.create_at(ZonedDateTime.now())
						.post(postEntity)
						.user(userEntity)
						.map(mapEntity)
						.build();
			}else {
				ret.setMap(mapEntity);
			}
			
			
			ret = mapRelationRepository.save(ret);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RelationService.setMapRelationSave : {}",e);
			ret = null;
		}
		
		return ret;
	}
	
	
	public PostRelationEntity setPostRelationSave(UserEntity userEntity,PostEntity postEntity,String confirmFlag) {
		PostRelationEntity ret = null;
		try {
			ret = postRelationRepository.findByUserAndPost(userEntity, postEntity);
			if(ret ==null) {
				if(confirmFlag.equals("Y")) {
					ret = null;
				}else {
					ret = PostRelationEntity.builder()
							.post(postEntity)
							.user(userEntity)
							.confirm_flag(confirmFlag)
							.create_at(ZonedDateTime.now())
							.build();
				}
				
			}else {
				ret.setConfirm_flag(confirmFlag);
			}
			
			ret = postRelationRepository.save(ret);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RelationService.setPostRelationSave : {}",e);
			ret = null;
		}
		
		return ret;
	}
	
	
	public UserRelationEntity setUserRelationSave(UserEntity userEntity,UserEntity friendEntity,String flag) {
		UserRelationEntity ret = null;
		try {
			UserRelationEntity tempEntity = userRelationRepository.findByUserToUser(userEntity, friendEntity);
			if(tempEntity !=null) {
				if(tempEntity.getApprove_flag().equals("N")) {
					userRelationRepository.delete(tempEntity);
					ret = UserRelationEntity.builder()
							.create_at(ZonedDateTime.now())
							.approve_flag(flag)
							.user(userEntity)
							.friend(friendEntity)
							.confirm_flag("N")
							.build();
					
					ret = userRelationRepository.save(ret);
				}else {
					ret = null;
				}
			}else {
				ret = UserRelationEntity.builder()
						.create_at(ZonedDateTime.now())
						.approve_flag(flag)
						.user(userEntity)
						.friend(friendEntity)
						.confirm_flag("N")
						.build();
				
				ret = userRelationRepository.save(ret);
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RelationService.setUserRelationSave : {}",e);
			ret = null;
		}
		return ret;
	}

	public List<FileRelationEntity> getFileRelationInfo(PostEntity postEntity) {
		List<FileRelationEntity> ret = null;
		try {
			ret = fileRelationRepository.findByPostSid(postEntity);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RelationService.getFileRelationInfo : {}",e);
			ret = null;
		}
		return ret;
	}
	
	public MapEntity getMapRelInfo(UserEntity userEntity,PostEntity postEntity) {
		MapEntity ret = null;
		try {
			MapRelationEntity relEntity = mapRelationRepository.findByPostANDUser(userEntity, postEntity);
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
	
	public List<UserRelationEntity> getFriendRelInfo(UserEntity userEntity){
		List<UserRelationEntity> ret = null;
		try {
			ret = userRelationRepository.findByIdAndFriend(userEntity);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RelationService.getFriendRelInfo : {}",e);
			ret = null;
		}
		return ret;
	}
	
	public List<UserRelationEntity> getFriendRelWait(UserEntity userEntity){
		List<UserRelationEntity> ret = null;
		try {
			ret = userRelationRepository.findByFriendAndWait(userEntity);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RelationService.getFriendRelWait : {}",e);
			ret = null;
		}
		
		return ret;
		
	}
	
	public UserRelationEntity getRelInfo(Long sid) {
		UserRelationEntity ret = null;
		try {
			ret = userRelationRepository.findById(sid).orElse(null);
			if(ret.getConfirm_flag().equals("N")) {
				ret.setConfirm_flag("Y");
			}
			ret = userRelationRepository.save(ret);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RelationService.getRelInfo : {}",e);
			ret = null;
		}
		return ret;
	}
	
	@Transactional
	public UserRelationEntity setRelApprove(Long sid,String approveFlag) {
		UserRelationEntity ret = null;
		try {
			ret = userRelationRepository.findById(sid).orElse(null);
			ret.setConfirm_flag("N");
			ret.setApprove_flag(approveFlag);
			ret.setUpdate_at(ZonedDateTime.now());
			ret = userRelationRepository.save(ret);
			if(ret.getApprove_flag().equals("Y")) {
				
				if(ret.getUser().getFriendsLength()==null) {
					ret.getUser().setFriendsLength(0);
				}
				if(ret.getFriend().getFriendsLength()==null) {
					ret.getFriend().setFriendsLength(0);
				}
				ret.getUser().incrementFriends();
				ret.getFriend().incrementFriends();
				
				UserEntity userEntity = userRepository.save(ret.getUser());
				UserEntity friendEntity = userRepository.save(ret.getFriend());
				
				ret.setUser(userEntity);
				ret.setFriend(friendEntity);
			}
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RelationService.setRelApprove : {}",e);
			ret = null;
		}
		return ret;
	}
	
	public List<UserRelationEntity> getRelApproveYesOrNo(UserEntity userEntity){
		List<UserRelationEntity> ret = new ArrayList<UserRelationEntity>();
		try {
			ZonedDateTime oneWeekAgo = ZonedDateTime.now().minusWeeks(1);
			ret = userRelationRepository.findByApproveYesOrNoConYes(userEntity,oneWeekAgo);
			List<UserRelationEntity> ret2 = userRelationRepository.findByApproveYesOrNoConNo(userEntity);
			ret.addAll(ret2);
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RelationService.getRelUserToFriend : {}",e);
			ret = null;
		}
		return ret;
	}
	
	public List<PostEntity> getPostRelInfo(UserEntity userEntity,int page){
		List<PostEntity> ret = new ArrayList<PostEntity>();
		try {
			Pageable pageable = PageRequest.of(page,10,Sort.by("create_at").descending());
			Page<PostRelationEntity> postPage = postRelationRepository.findByUserPostRel(userEntity, pageable);
			for(PostRelationEntity row:postPage.getContent()) {
				ret.add(row.getPost());
			}
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RelationService.getPostRelInfo : {}",e);
			ret = null;
		}
		return ret;
	}
	
	public List<PostRelationEntity> getPostRelToAlert(UserEntity userEntity){
		List<PostRelationEntity> ret = new ArrayList<PostRelationEntity>();
		try {
			ZonedDateTime oneWeekAgo = ZonedDateTime.now().minusWeeks(1);
			ret = postRelationRepository.findByUserPostToDate(userEntity, oneWeekAgo);
			List<PostRelationEntity> ret2 = postRelationRepository.findByUserPost(userEntity);
			ret.addAll(ret2);
		}catch (Exception e) {
			log.error("RelationService.getPostRelToAlert : {}",e);
			ret = null;
			// TODO: handle exception
		}
		return ret;
	}
	
	public MapRelationEntity setMapRelFav(UserEntity userEntity,MapEntity mapEntity,String favFlag){
		MapRelationEntity ret = null;
		try {
			ret = mapRelationRepository.findByUserAndMapFav(userEntity,mapEntity);
			if(ret == null) {
				if(favFlag.equals("Y")) {
					ret = MapRelationEntity.builder()
							.create_at(ZonedDateTime.now())
							.map(mapEntity)
							.post(null)
							.user(userEntity)
							.build();
					ret = mapRelationRepository.save(ret);
				}else {
					ret = MapRelationEntity.builder()
							.sid(null)
							.build();
				}
			}else {
				if(favFlag.equals("N")) {
					mapRelationRepository.delete(ret);
					ret.setSid(null);
				}else {
					ret = MapRelationEntity.builder()
							.sid(null)
							.build();
				}
			}
			
			
			
		}catch (Exception e) {
			log.error("RelationService.getPostRelToAlert : {}",e);
			ret = null;
			// TODO: handle exception
		}
		return ret;
	}

	public List<MapEntity> getMapRelFav(UserEntity userEntity){
		List<MapEntity> ret = new ArrayList<MapEntity>();
		try {
			List<MapRelationEntity> relArray = mapRelationRepository.findByMapFav(userEntity);
			if(relArray==null) {
				throw new Exception("즐겨찾기 없음");
			}
			for(MapRelationEntity row: relArray) {
				ret.add(row.getMap());
			}
			
			
		}catch (Exception e) {
			log.error("RelationService.getPostRelToAlert : {}",e);
			ret = null;
			// TODO: handle exception
		}
		return ret;
	}

	public RestRelationEntity setRestRelSave(RestEntity restEntity,UserEntity userEntity,String flag){
		RestRelationEntity ret = null;
		try {
			ret = restRelationRepository.findByUserAndRest(userEntity, restEntity).orElse(null);
			if(ret==null) {
				ret = RestRelationEntity.builder()
								.confirm_flag(flag)
								.create_at(ZonedDateTime.now())
								.rest(restEntity)
								.user(userEntity)
								.build();
				ret = restRelationRepository.save(ret);
			}else {
				ret.setConfirm_flag(flag);
				ret = restRelationRepository.save(ret);
			}
			
			
		}catch (Exception e) {
			log.error("RelationService.setRestRelSave : {}",e);
			ret = null;
			// TODO: handle exception
		}
		return ret;
	}
	
	public List<RestRelationEntity> getRestRelInfo(UserEntity userEntity){
		List<RestRelationEntity> ret = new ArrayList<RestRelationEntity>();
		try {
			ZonedDateTime oneWeekAgo = ZonedDateTime.now().minusWeeks(1);
			ret = restRelationRepository.findByUserList(userEntity);
			List<RestRelationEntity> ret2 = restRelationRepository.findByUserListToDate(userEntity, oneWeekAgo);
			ret.addAll(ret2);
						
		}catch (Exception e) {
			log.error("RelationService.getRestRelInfo : {}",e);
			ret = null;
			// TODO: handle exception
		}
		return ret;
	}
	
	public RestRelationEntity setRestRelApprove(Long restRelSid){
		RestRelationEntity ret = null;
		try {
			ret = restRelationRepository.findById(restRelSid).orElse(null);
			if(ret==null) {
				throw new Exception(restRelSid.toString());
			}
			ret.setConfirm_flag("Y");
			ret = restRelationRepository.save(ret);
		}catch (Exception e) {
			log.error("RelationService.setRestRelApprove : {}",e);
			ret = null;
			// TODO: handle exception
		}
		return ret;
	}

	public int findByMapAndUserCount(UserEntity userEntity,MapEntity mapEntity){
		int ret = 0;
		try {
			ret = mapRelationRepository.findByMapAndUserCount(userEntity, mapEntity);
						
		}catch (Exception e) {
			log.error("RelationService.findByMapAndUserCount : {}",e);
			ret = 0;
			// TODO: handle exception
		}
		return ret;
	}
	
	
}
