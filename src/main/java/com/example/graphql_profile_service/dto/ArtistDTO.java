package com.example.graphql_profile_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistDTO {
    private String spotifyId;
    private String name;
    private List<String> genres;
    private Integer popularity;
    private String imageUrl;
    private String externalUrl;
    private Integer followers;
}
