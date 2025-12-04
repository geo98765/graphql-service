package com.example.graphql_profile_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "favorite_genres")
public class FavoriteGenre {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_genre_id", nullable = false)
    private Integer favoriteGenreId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_genre_id")
    private MusicGenre musicGenre;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;
}