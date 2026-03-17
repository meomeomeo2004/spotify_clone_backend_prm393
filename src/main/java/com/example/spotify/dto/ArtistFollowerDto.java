package com.example.spotify.dto;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
public class ArtistFollowerDto {
        private Long userId;
        private List<Long> artistIds;

        public ArtistFollowerDto(Long userId, List<Long> artistIds){
            this.userId = userId;
            this.artistIds = artistIds;
        }
}

