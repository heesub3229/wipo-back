package com.wipo.Entity;

import java.time.ZonedDateTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "map_relation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapRelationEntity {
	    
	@EmbeddedId
	private MapRelationId id;
	
	@OneToOne
	@JoinColumn(name = "user_sid", referencedColumnName = "sid", nullable = false, insertable = false, updatable = false)
	private UserEntity user;
	
	@OneToOne
	@JoinColumn(name = "map_sid", referencedColumnName = "sid", nullable = false, insertable = false, updatable = false)
	private MapEntity map;
	
	@OneToOne(optional = true)
	@JoinColumn(name = "post_sid", referencedColumnName = "sid",nullable = true)
	private PostEntity post;
	
	@Column(name="create_at", nullable = false)
   private ZonedDateTime create_at;
	
}
