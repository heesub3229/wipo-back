package com.wipo.Entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "map_master")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sid; 

    @Column(nullable = false)
    private double x;
    
    @Column(nullable = false)
    private double y;
    
    @Column( length = 100)
    private String placeName;
    @Column( length = 200)
    private String addressName;
    @Column( length = 50)
    private String region_1depth_name;
    @Column( length = 50)
    private String region_2depth_name;
    @Column( length = 50)
    private String region_3depth_name;
    @Column( length = 1,nullable = false)
    private String type;
    
    
    @Column(name="create_at", nullable = false)
    private ZonedDateTime create_at;
}
