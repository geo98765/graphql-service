// package com.example.rockstadiumgraphql.mapper;

// import java.util.List;
// import java.util.stream.Collectors;

// import org.springframework.stereotype.Component;

// import com.example.rockstadiumgraphql.dto.ArtistDTO;
// import com.example.rockstadiumgraphql.dto.MusicGenreDTO;
// import com.example.rockstadiumgraphql.dto.UserPreferenceDTO;
// import com.example.rockstadiumgraphql.model.Artist;
// import com.example.rockstadiumgraphql.model.FavoriteArtist;
// import com.example.rockstadiumgraphql.model.FavoriteGenre;
// import com.example.rockstadiumgraphql.model.MusicGenre;
// import com.example.rockstadiumgraphql.model.UserPreference;

// /**
//  * Mapper para convertir entidades a DTOs
//  * Mapper for converting entities to DTOs
//  */
// @Component
// public class UserPreferenceMapper {
    
//     private static final int MAX_FAVORITE_ARTISTS = 40;
//     private static final int MAX_FAVORITE_GENRES = 30;
    
//     /**
//      * Convertir UserPreference a DTO
//      * Convert UserPreference to DTO
//      */
//     public UserPreferenceDTO toDTO(UserPreference preference, 
//                                    List<FavoriteArtist> favoriteArtists,
//                                    List<FavoriteGenre> favoriteGenres) {
        
//         // Mapear artistas favoritos | Map favorite artists
//         List<ArtistDTO> artistDTOs = favoriteArtists.stream()
//                 .map(fa -> toArtistDTO(fa.getArtist()))
//                 .collect(Collectors.toList());
        
//         // Mapear géneros favoritos | Map favorite genres
//         List<MusicGenreDTO> genreDTOs = favoriteGenres.stream()
//                 .map(fg -> toGenreDTO(fg.getMusicGenre()))
//                 .collect(Collectors.toList());
        
//         return UserPreferenceDTO.builder()
//                 .userPreferenceId(preference.getUserPreferenceId())
//                 .profileId(preference.getProfile().getProfileId())
//                 .searchRadius(preference.getSearchRadius())
//                 .emailNotifications(preference.getEmailNotifications())
//                 .favoriteArtists(artistDTOs)
//                 .favoriteGenres(genreDTOs)
//                 .favoriteArtistsCount(artistDTOs.size())
//                 .favoriteGenresCount(genreDTOs.size())
//                 .maxFavoriteArtists(MAX_FAVORITE_ARTISTS)
//                 .maxFavoriteGenres(MAX_FAVORITE_GENRES)
//                 .build();
//     }
    
//     /**
//      * Convertir Artist a DTO
//      * Convert Artist to DTO
//      */
//     public ArtistDTO toArtistDTO(Artist artist) {
//         if (artist == null) return null;
        
//         return ArtistDTO.builder()
//                 .artistId(artist.getArtistId())
//                 .spotifyId(artist.getSpotifyId())
//                 .name(artist.getName())
//                 .build();
//     }
    
//     /**
//      * Convertir MusicGenre a DTO
//      * Convert MusicGenre to DTO
//      */
//     public MusicGenreDTO toGenreDTO(MusicGenre genre) {
//         if (genre == null) return null;
        
//         return MusicGenreDTO.builder()
//                 .musicGenreId(genre.getMusicGenreId())
//                 .name(genre.getName())
//                 .description(genre.getDescription())
//                 .build();
//     }
// }