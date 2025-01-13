package com.wipo.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wipo.DTO.FavSaveDTO;
import com.wipo.DTO.ResponseDTO;
import com.wipo.Entity.MapEntity;
import com.wipo.Entity.MapRelationEntity;
import com.wipo.Entity.UserEntity;
import com.wipo.Repository.MapRepository;
import com.wipo.Repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MapService {

	@Autowired
	private MapRepository mapRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	private final RelationService relationService;
	
	public MapService(RelationService relationService) {
		this.relationService = relationService;
	}
	
	@Transactional
	public ResponseDTO<?> setFavMap(Long userSid,List<FavSaveDTO> favArray){
		ResponseDTO<?> ret = null;
		List<MapEntity> retEntity = new ArrayList<MapEntity>();
		try {
			UserEntity userEntity = userRepository.findById(userSid).orElse(null);
			if(userEntity==null) {
				throw new Exception("회원정보에러");
			}
			
			for(FavSaveDTO row: favArray) {
				MapEntity tempEntity = mapRepository.findByXAndY(row.getMap().getX(), row.getMap().getY());
				if(tempEntity==null) {
					row.getMap().setCreate_at(ZonedDateTime.now());
					tempEntity = mapRepository.save(row.getMap());
				}
				MapRelationEntity relEntity = relationService.setMapRelFav(userEntity, tempEntity,row.getFavFlag());
				if(relEntity==null) {
					throw new Exception("즐겨찾기 저장 에러");
				}
				if(relEntity.getSid()!=null) {
					retEntity.add(relEntity.getMap());
				}
				
				
			}
			
			
			ret = ResponseDTO.<List<MapEntity>>builder()
						.errFlag(false)
						.data(retEntity)
						.resDate(ZonedDateTime.now())
						.build();
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("MapService.setFavMap : {}",e);
			ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
		}
		return ret;
	}
	
}
