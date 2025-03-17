package com.wipo.Entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rest_relation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestRelationEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sid; 
	
	@ManyToOne
	@JoinColumn(name = "user_sid", referencedColumnName = "sid", nullable = false)
	private UserEntity user;
	
	@ManyToOne
	@JoinColumn(name = "rest_sid", referencedColumnName = "sid", nullable = false)
	private RestEntity rest;
	
	@Column(name="confirm_flag",length = 1)
	private String confirm_flag;
	
	@Column(name="create_at", nullable = false)
	private ZonedDateTime create_at;
}
