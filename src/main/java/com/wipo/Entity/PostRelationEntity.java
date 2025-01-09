package com.wipo.Entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
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
@Table(name = "post_relation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRelationEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sid; 
	
	@ManyToOne
	@JoinColumn(name = "user_sid", referencedColumnName = "sid", nullable = false)
	private UserEntity user;
	
	@ManyToOne
	@JoinColumn(name = "post_sid", referencedColumnName = "sid", nullable = false)
	private PostEntity post;
	
	@Column(name="confirm_flag",length = 1)
	private String confirm_flag;
	
	@Column(name="create_at", nullable = false)
	private ZonedDateTime create_at;
	
}
