package com.wipo.Entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.CustomLog;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long sid; 

	    @Column(nullable = false, unique = true, length = 100)
	    private String email;
	    
	    @Column(nullable = false, length = 100)
	    private String name;

	    @Column(length = 255)
	    private String password; 

	    @Column(name = "login_type",nullable = false ,length=1)
	    private String login_type;
	    
	    @Column(name = "dateBirth",length = 20)
	    private String dateBirth;
	    
	    @Column(nullable = false ,name="isPrivacy")
	    private boolean isPrivacy;
	    
	    @Column(name="create_at", nullable = false)
	    private Date create_at;
	    
	    
	
}
