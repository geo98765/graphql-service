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
public class PreferenceBasicDTO {
    private Integer userPreferenceId;
    private Integer profileId;
    private Double searchRadius;
    private Boolean emailNotifications;
}