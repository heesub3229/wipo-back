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
@Table(name = "user_relation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRelationEntity {
	
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sid; 
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "user_sid", referencedColumnName = "sid")
	private UserEntity user;
	@ManyToOne(optional = false)
	@JoinColumn(name = "friend_sid", referencedColumnName = "sid")
	private UserEntity friend;
	
	@Column(name="approve_flag",length = 1)
    private String approve_flag;
	
	@Column(name="confirm_flag",length = 1)
	private String confirm_flag;
	
	@Column(name="create_at", nullable = false)
    private ZonedDateTime create_at;
	
	@Column(name="update_at")
    private ZonedDateTime update_at;

}
