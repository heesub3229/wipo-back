package com.wipo.Entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "file_master")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sid; 

    @Column(nullable = false, length = 255)
    private String filename;
    
    @Column(nullable = false, length = 255)
    private String filepath;

    @Column(name="create_at", nullable = false)
    private ZonedDateTime create_at;

}
