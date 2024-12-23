package com.wipo.Entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MapRelationId implements Serializable{
	
	 @Column(name = "user_sid", nullable = false)
	private Long user_sid;
	 @Column(name = "map_sid", nullable = false)
	private Long map_sid;

}
