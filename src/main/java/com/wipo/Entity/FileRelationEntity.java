package com.wipo.Entity;

import java.time.ZonedDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
	
	 @EmbeddedId
	 private FileRelationId id; // 복합키 클래스 사용

	
	@OneToOne
    @JoinColumn(name = "file_sid", referencedColumnName = "sid", insertable = false, updatable = false)
    private FileEntity file;
	
	@OneToOne
	@JoinColumn(name = "post_sid", referencedColumnName = "sid", insertable = false, updatable = false)
	private PostEntity post;
	
	@Column(name="create_at", nullable = false)
    private ZonedDateTime create_at;
	
}


