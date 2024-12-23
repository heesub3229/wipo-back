package com.wipo.Entity;

import java.io.Serializable;
import java.time.ZonedDateTime;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRelationId implements Serializable {

	@OneToOne
	@JoinColumn(name = "user_sid", referencedColumnName = "sid", nullable = false)
	private UserEntity user;
	
	@OneToOne
	@JoinColumn(name = "post_sid", referencedColumnName = "sid", nullable = false)
	private PostEntity post;
	
	
}
