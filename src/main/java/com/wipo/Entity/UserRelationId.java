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
public class UserRelationId implements Serializable {
	
	@OneToOne(optional = false)
	@JoinColumn(name = "user_sid", referencedColumnName = "sid")
	private UserEntity user;
	@OneToOne(optional = false)
	@JoinColumn(name = "friend_sid", referencedColumnName = "sid")
	private UserEntity friend;

}
