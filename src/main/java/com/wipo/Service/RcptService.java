package com.wipo.Service;


import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.wipo.DTO.GraphSendDTO;
import com.wipo.DTO.RcptSendDTO;
import com.wipo.DTO.ResponseDTO;
import com.wipo.Entity.RcptEntity;
import com.wipo.Entity.UserEntity;
import com.wipo.Repository.RcptRepository;
import com.wipo.Repository.UserRelationRepository;
import com.wipo.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RcptService {
	
	@Autowired
	private RcptRepository rcptRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	public ResponseDTO<?> setRcptSave(Long userSid,RcptEntity rcptEntity){
		ResponseDTO<?> ret = null;
		try {
			
			UserEntity userEntity = userRepository.findById(userSid).orElse(null);
			if(userEntity == null) {
				throw new Exception("유저정보에러");
			}
			RcptEntity saveEntity = null;
			if(rcptEntity.getSid()==null) {
				saveEntity = RcptEntity.builder()
						.amount(rcptEntity.getAmount())
						.category(rcptEntity.getCategory())
						.create_at(ZonedDateTime.now())
						.create_user_sid(userEntity)
						.date(rcptEntity.getDate())
						.memo(rcptEntity.getMemo())
						.payment(rcptEntity.getPayment())
						.type(rcptEntity.getType())
						.build();

				saveEntity = rcptRepository.save(saveEntity);
			}else {
				saveEntity = rcptRepository.findById(rcptEntity.getSid()).orElse(null);
				if(saveEntity==null) {
					throw new Exception("해당자료없음");
				}else {
					saveEntity.setAmount(rcptEntity.getAmount());
					saveEntity.setCategory(rcptEntity.getCategory());
					saveEntity.setDate(rcptEntity.getDate());
					saveEntity.setMemo(rcptEntity.getMemo());
					saveEntity.setPayment(rcptEntity.getPayment());
					saveEntity.setType(rcptEntity.getType());
					saveEntity = rcptRepository.save(saveEntity);
				}
			}
			
			
			
			ret = ResponseDTO.<RcptEntity>builder()
					.errFlag(false)
					.resDate(ZonedDateTime.now())
					.data(saveEntity)
					.build();
			
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RcptService.setRcptSave : {}",e);
			ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
		}
		return ret;
	}
	
	public ResponseDTO<?> getRcpInfo(Long userSid,int page){
		ResponseDTO<?> ret = null;
		List<RcptEntity> res = null;
		try {
			UserEntity userEntity = userRepository.findById(userSid).orElse(null);
			if(userEntity == null) {
				throw new Exception("유저정보에러");
			}
			Integer defaultDay = userEntity.getDefaultDay();
			
			if(defaultDay == null||defaultDay == 0) {
				defaultDay = 1;
			}
			if(page==0) {
				throw new Exception("페이지 선택이 잘못됫습니다.");
			}
			
			LocalDate now = LocalDate.now();
			String startDate = "";
			String endDate = "";
			if(now.getDayOfMonth()<defaultDay) {
				startDate = UtilService.convDateToStringDay(page-1, defaultDay, now.minusMonths(page));
				endDate = UtilService.convDateToStringDay(0, 0, UtilService.convStringToDate(startDate).plusMonths(1).minusDays(1));
				
			}else {
				startDate = UtilService.convDateToStringDay(page-1, defaultDay, now);
				endDate = UtilService.convDateToStringDay(0, 0, UtilService.convStringToDate(startDate).plusMonths(1).minusDays(1));
				
			
			}
						
			res = rcptRepository.findByDateAndUser(startDate,endDate, userEntity);
			
			
			
			ret = ResponseDTO.<List<RcptEntity>>builder()
					.errFlag(false)
					.resDate(ZonedDateTime.now())
					.data(res)
					.build();
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RcptService.getRcpInfo : {}",e);
			ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
		}
		return ret;
	}
	
	public ResponseDTO<?> setRcptDelete(Long rcptSid){
		ResponseDTO<?> ret = null;
		try {
			RcptEntity rcptEntity = rcptRepository.findById(rcptSid).orElse(null);
			if(rcptEntity == null) {
				throw new Exception("존재하지않는데이터");
			}
			rcptRepository.delete(rcptEntity);
			
			ret = ResponseDTO.<String>builder()
					.errFlag(false)
					.resDate(ZonedDateTime.now())
					.build();
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RcptService.setRcptDelete : {}",e);
			ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
		}
		return ret;
	}
	
	
	public Map<String,Long> getRcptMonthAmount(UserEntity user){
		Map<String,Long> ret = new HashMap<String,Long>();
		List<String> startDate = new ArrayList<String>();
		List<String> endDate = new ArrayList<String>();
		try {
			Integer defaultDay = user.getDefaultDay();
			if(defaultDay==null||defaultDay == 0) {
				defaultDay = 1;
			}
			LocalDate date = LocalDate.now();
			Integer nowDay = date.getDayOfMonth();
			
			if(nowDay<defaultDay) {
				startDate.add(UtilService.convDateToStringDay(1, defaultDay, date));
				startDate.add(UtilService.convDateToStringDay(2, defaultDay, date));
				endDate.add(UtilService.convDateToStringDay(0, 0, UtilService.convStringToDate(startDate.get(0)).plusMonths(1).minusDays(1)));
				endDate.add(UtilService.convDateToStringDay(0, 0, UtilService.convStringToDate(startDate.get(1)).plusMonths(1).minusDays(1)));
			}else {
				startDate.add(UtilService.convDateToStringDay(0, defaultDay, date));
				startDate.add(UtilService.convDateToStringDay(1, defaultDay, date));
				endDate.add(UtilService.convDateToStringDay(0, 0, UtilService.convStringToDate(startDate.get(0)).plusMonths(1).minusDays(1)));
				endDate.add(UtilService.convDateToStringDay(0, 0, UtilService.convStringToDate(startDate.get(1)).plusMonths(1).minusDays(1)));
			}
			for(int i = 0;i<startDate.size();i++) {
				if(startDate.get(i).equals("")||endDate.get(i).equals("")) {
					throw new Exception("가계부에러");
				}
				String startEnd = startDate.get(i)+endDate.get(i);
				List<Object[]> resArray = rcptRepository.getMonthSumAmount(startDate.get(i), endDate.get(i), user);
				
				if(resArray.size()>0) {
					for(Object[] row:resArray) {
						
						String type = (String)row[0];
						Long sumAmount = (Long)row[2] == null ? 0L : (Long)row[2];
						ret.put(startEnd+";"+type,sumAmount);
					}
				}else {
					ret.put(startEnd+";"+"I",0L);
					ret.put(startEnd+";"+"O",0L);
				}
				
				
			}
			
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RcptService.getRcptMonthAmount : {}",e);
			ret= null;
		}
		return ret;
		
	}
		
	public ResponseDTO<?> getRcptGraphMonth(Long userSid,int page){
		ResponseDTO<?> ret = null;
		List<GraphSendDTO> res = new ArrayList<GraphSendDTO>();
		try {
			UserEntity userEntity = userRepository.findById(userSid).orElse(null);
			if(userEntity==null) {
				throw new Exception("사용자 정보에러");
			}
			Integer defaultDay = userEntity.getDefaultDay();
			if(defaultDay==null||defaultDay==0) {
				defaultDay = 1;
			}
			
			LocalDate now = LocalDate.now();
			
			
			for(int i = 0;i<6;i++) {
				String startDate = "";
				if(now.getDayOfMonth()<defaultDay) {
					startDate = UtilService.convDateToStringDay(i, defaultDay, now.minusMonths(((page-1)*6)+1));
				}else {
					startDate = UtilService.convDateToStringDay(i, defaultDay, now.minusMonths((page-1)*6));
				}
				String endDate = UtilService.convDateToStringDay(0, 0, UtilService.convStringToDate(startDate).plusMonths(1).minusDays(1));
				String startEnd = UtilService.convDateToStringMonth(0, UtilService.convStringToDate(startDate));
				List<Object[]> resArray = rcptRepository.getMonthSumAmount(startDate, endDate, userEntity);
				if(resArray.size()>0) {
					GraphSendDTO dto = new GraphSendDTO();
					dto.setDate(startEnd);
					dto.setExpense(0L);
					dto.setIncome(0L);
					for(Object[] row : resArray) {
						Long sumAmount = (Long)row[1] == null ? 0L : (Long)row[1];
						String type = (String)row[0];
						if(type.equals("I")) {
							dto.setIncome(sumAmount);
						}else {
							dto.setExpense(sumAmount);
						}
					}
					res.add(dto);
				}else {
					GraphSendDTO tempDto = GraphSendDTO.builder()
							.date(startEnd)
							.expense(0L)
							.income(0L)
							.build();
					res.add(tempDto);
				}
				
			}
			
			Collections.reverse(res);
			ret = ResponseDTO.<List<GraphSendDTO>>builder()
					.errFlag(false)
					.resDate(ZonedDateTime.now())
					.data(res)
					.build();
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RcptService.getRcptGraphMonth : {}",e);
			ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
		}
		return ret;
	}
	
	public ResponseDTO<?> getRcptGraphDay(Long userSid,int page){
		ResponseDTO<?> ret = null;
		List<GraphSendDTO> res = new ArrayList<GraphSendDTO>();
		try {
			
			UserEntity userEntity = userRepository.findById(userSid).orElse(null);
			
			if(userEntity==null) {
				throw new Exception("유저정보에러");
			}
			Integer defaultDay = userEntity.getDefaultDay();
			if(defaultDay==null) {
				defaultDay = 1;
			}
			LocalDate now = LocalDate.now();
			for(int i=0;i<7;i++) {
				String date = UtilService.convDateToStringDay(0, 0, now.minusDays((page-1)*7+i));
				
				List<Object[]> convRes = rcptRepository.getMonthSumAmountToDate(date, userEntity);
				if(convRes.size()>0) {
					
					GraphSendDTO tempDto = new GraphSendDTO();
					tempDto.setExpense(0L);
					tempDto.setIncome(0L);
					tempDto.setDate(date);
					for(Object[] row:convRes) {
						
						Long sumAmount = (Long)row[2] == null ? 0L : (Long)row[2];
						String type = (String)row[1];
						if(type.equals("I")) {
							tempDto.setIncome(sumAmount);
						}else {
							tempDto.setExpense(sumAmount);
						}
					}
					res.add(tempDto);
					
				}else {
					GraphSendDTO tempDto = GraphSendDTO.builder()
															.date(date)
															.income(0L)
															.expense(0L)
															.build();
					res.add(tempDto);
				}
				
			}
			Collections.reverse(res);
			ret = ResponseDTO.<List<GraphSendDTO>>builder()
					.errFlag(false)
					.resDate(ZonedDateTime.now())
					.data(res)
					.build();
		}catch (Exception e) {
			// TODO: handle exception
			log.error("RcptService.getRcptGraphDay : {}",e);
			ret = ResponseDTO.<String>builder()
					.errFlag(true)
					.resDate(ZonedDateTime.now())
					.data(e.getMessage())
					.build();
		}
		return ret;
	}
	
//	public ResponseDTO<?> setRcptSave(RcptEntity rcptEntity){
//		ResponseDTO<?> ret = null;
//		try {
//			
//		}catch (Exception e) {
//			// TODO: handle exception
//			log.error("RcptService.setRcptSave : {}",e);
//			ret = null;
//		}
//		return ret;
//	}
	
}
