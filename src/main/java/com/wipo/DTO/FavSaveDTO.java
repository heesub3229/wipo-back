package com.wipo.DTO;

import java.time.LocalDateTime;

import com.wipo.Entity.MapEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavSaveDTO {
	private MapEntity map;
	private String favFlag;
}
