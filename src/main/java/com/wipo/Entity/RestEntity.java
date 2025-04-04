package com.wipo.Entity;

import java.math.BigDecimal;
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
@Table(name = "rest_master")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sid; 
	
	@Column(nullable = false, length = 1)
	private String category;
	
	@Column( length = 100)
    private String placeName;
	
	@Column( length = 50)
    private String menuName;
	
	@Column( length = 200)
    private String memo;
	
	@Column(precision = 2, scale = 1)
	private BigDecimal rating;
	
	@OneToOne(optional = true)
    @JoinColumn(name = "file_sid", referencedColumnName = "sid",nullable = true)
    private FileEntity file;
	
	@ManyToOne(optional = true)
    @JoinColumn(name = "map_sid", referencedColumnName = "sid")
    private MapEntity map;
	
	@Column(name="create_at", nullable = false)
    private ZonedDateTime create_at;
	
	@ManyToOne(optional = false)
    @JoinColumn(name="create_user_sid",referencedColumnName = "sid")
    private UserEntity create_user_sid;
	
}
