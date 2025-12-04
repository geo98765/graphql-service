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
public class UserPreferencesDTO {
    private Integer userPreferenceId;
    private Integer profileId;
    private Double searchRadius;
    private Boolean emailNotifications;
    private List<ArtistDTO> favoriteArtists;
    private List<MusicGenreDTO> favoriteGenres;
    private Integer favoriteArtistsCount;
    private Integer favoriteGenresCount;
    private Integer maxFavoriteArtists;
    private Integer maxFavoriteGenres;
}
