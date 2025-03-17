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
@Table(name = "rcpt_master")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RcptEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sid; 
	
	@Column(nullable = false, length = 1)
	private String type;
	
	@Column(nullable = false)
	private Long amount;
	
	@Column(nullable = false, length = 1)
	private String category;
	
	@Column(nullable = false, length = 1)
	private String payment;
	
	@Column(nullable = false, length = 20)
	private String date;
	
	@Column(nullable = true, length = 200)
	private String memo;
	
	@Column(name="create_at", nullable = false)
    private ZonedDateTime create_at;
	
	@ManyToOne(optional = false)
    @JoinColumn(name="create_user_sid",referencedColumnName = "sid")
    private UserEntity create_user_sid;
	
}
