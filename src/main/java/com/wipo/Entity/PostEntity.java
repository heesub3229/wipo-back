package com.wipo.Entity;

import java.time.ZonedDateTime;

import org.hibernate.annotations.ManyToAny;

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
@Table(name = "post_master")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sid; 

    @Column(nullable = false, length = 20)
    private String date;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name="create_at", nullable = false)
    private ZonedDateTime create_at;
	
    @Column(name="update_at")
    private ZonedDateTime update_at;
    
    @ManyToOne
    @JoinColumn(name="create_user_sid",referencedColumnName = "sid")
    private UserEntity create_user_sid;
}
