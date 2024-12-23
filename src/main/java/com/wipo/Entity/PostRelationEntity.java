package com.wipo.Entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_relation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRelationEntity {

	@EmbeddedId
	private PostRelationId id;
	
	@Column(name="create_at", nullable = false)
	private ZonedDateTime create_at;
	
}
