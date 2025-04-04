package com.wipo.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wipo.Entity.FileEntity;
import com.wipo.Entity.FileRelationEntity;
import com.wipo.Entity.PostEntity;
import com.wipo.Repository.FileRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileService {
	
	@Value("${file.path}")
	private String filePath;
	
	@Autowired
	private FileRepository fileRepository;
	
	@Autowired
	public RelationService relationService;
	
	@Transactional
	public List<FileEntity> setFileDBSave(List<MultipartFile> files){
		List<FileEntity> ret = new ArrayList<FileEntity>();
		try {
			
			for(MultipartFile row:files) {
				FileEntity fileEntity =  setFileSave(row);
				if(fileEntity ==null) {
					throw new Exception("파일저장에러");
				}
				fileEntity = fileRepository.save(fileEntity);
				ret.add(fileEntity);
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			if(ret.size()>0) {
				setFileDelete(ret);
			}
			log.error("FileService.setFileDBSave : {}",e);
			ret = null;
		}
		
		
		return ret;
	}
	
	public FileEntity setFileDBSaveOne(MultipartFile file) {
		FileEntity ret = null;
		try {
			ret = setFileSave(file);
			if(ret ==null) {
				throw new Exception("파일저장에러");
			}
			ret = fileRepository.save(ret);
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("FileService.setFileDBSaveOne : {}",e);
			ret = null;
		}
		return ret;
	}
	
	public void setFileDelete(List<FileEntity> files) {
		Path path=null;
		try {
			for(FileEntity row:files) {
				path = Paths.get(row.getFilepath());
				Files.deleteIfExists(path);
				path = null;
			}
		}catch (IOException e) {
			// TODO: handle exception
			log.error("FileService.setFileDelete : {}",e);
		}
	}
	
	public void setFileDeleteOne(FileEntity files) {
		Path path=null;
		try {
				path = Paths.get(files.getFilepath());
				Files.deleteIfExists(path);
				path = null;
		}catch (IOException e) {
			// TODO: handle exception
			log.error("FileService.setFileDelete : {}",e);
		}
	}
	
	private FileEntity setFileSave(MultipartFile file) {
		FileEntity ret = null;
		try {
			ZonedDateTime time = ZonedDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
	        String formattedDate = time.format(formatter);
	        Path path = Paths.get(filePath);
	        
	        if(!Files.exists(path)) {
	        	Files.createDirectories(path);
	        }
	        path = Paths.get(filePath+formattedDate+"/");
	        if(!Files.exists(path)) {
	        	Files.createDirectories(path);
	        }
	        
	        String fileName = file.getOriginalFilename();
	        String convName = UtilService.getFilenameToDate(fileName);
	        byte[] bytes = file.getBytes();
	        path = Paths.get(filePath+formattedDate+"/"+convName);
	      
	        Path retPath = Files.write(path, bytes);
	        
	        ret = FileEntity.builder()
	        		.create_at(ZonedDateTime.now())
	        		.filename(convName)
	        		.filepath(retPath.toString())
	        		.build();
	        
		}catch (IOException e) {
			// TODO: handle exception
			log.error("FileService.setFileSave : {}",e);
			ret = null;
		}catch (Exception e) {
			// TODO: handle exception
			log.error("FileService.setFileSave : {}",e);
			ret = null;
		}
		return ret;
	}

	public List<FileEntity> getFileInfo(PostEntity postEntity){
		List<FileEntity> ret = new ArrayList<FileEntity>();
		try {
			List<FileRelationEntity> relArray = relationService.getFileRelationInfo(postEntity);
			if(relArray==null) {
				throw new Exception("파일정보없음");
			}
			for(FileRelationEntity row: relArray) {
				ret.add(row.getFile());
			}
		}catch (Exception e) {
			// TODO: handle exception
			log.error("FileService.getFileInfo : {}",e);
			ret = null;
		}
		return ret;
	}
	
}
