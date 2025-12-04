package com.example.graphql_profile_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


/**
 * DTO de respuesta para géneros musicales
 * Response DTO for music genres
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicGenreDTO {
    private Integer musicGenreId;
    private String name;
    private String description;
}