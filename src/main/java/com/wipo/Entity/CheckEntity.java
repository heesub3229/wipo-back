package com.wipo.Entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "check_master")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckEntity {


	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sid; 
	
	@Column( length = 50,nullable = false)
    private String title;
	
	@Column(length = 50,nullable = false)
	private String object;
	
	@Column(length = 1,nullable = false)
	private String checkFlag;
	
	@Column(name="create_at", nullable = false)
    private ZonedDateTime create_at;
	
	@OneToOne(optional = false)
    @JoinColumn(name="create_user_sid",referencedColumnName = "sid")
    private UserEntity create_user_sid;
}
