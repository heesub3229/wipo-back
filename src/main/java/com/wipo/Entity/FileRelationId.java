package com.wipo.Entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileRelationId implements Serializable{
	@Column(name = "file_sid", nullable = false)
	private Long file_sid;
	@Column(name = "post_sid", nullable = false)
    private Long post_sid;

	
}
