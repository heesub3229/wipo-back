package com.wipo.Entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "file_relation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileRelationEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sid; 

	@ManyToOne
    @JoinColumn(name = "file_sid", referencedColumnName = "sid",nullable = false )
    private FileEntity file;
	
	@ManyToOne
	@JoinColumn(name = "post_sid", referencedColumnName = "sid",nullable = false)
	private PostEntity post;
	
	@Column(name="create_at", nullable = false)
    private ZonedDateTime create_at;
	
}


