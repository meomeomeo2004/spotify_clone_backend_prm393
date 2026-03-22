package com.example.spotify.service;

import com.example.spotify.dto.FollowingArtistDto;
import com.example.spotify.entity.Artist;
import com.example.spotify.entity.ArtistFollower;
import com.example.spotify.entity.ArtistFollowerId;
import com.example.spotify.entity.User;
import com.example.spotify.repository.ArtistFollowerRepository;
import com.example.spotify.repository.ArtistRepository;
import com.example.spotify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtistFollowerService {

    private final ArtistFollowerRepository artistFollowerRepository;
    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;

    /** Bulk-follow nhiều artist (dùng khi onboarding chọn nghệ sĩ). */
    public void saveUserArtists(Long userId, List<Long> artistIds) {
        User userProxy = userRepository.getReferenceById(userId);
        List<ArtistFollower> followers = artistIds.stream()
                .map(artistId -> {
                    Artist artistProxy = artistRepository.getReferenceById(artistId);
                    ArtistFollowerId compositeId = new ArtistFollowerId();
                    compositeId.setUserId(userId);
                    compositeId.setArtistId(artistId);
                    ArtistFollower follower = new ArtistFollower();
                    follower.setId(compositeId);
                    follower.setUser(userProxy);
                    follower.setArtist(artistProxy);
                    follower.setFollowedAt(LocalDateTime.now());
                    return follower;
                })
                .collect(Collectors.toList());
        artistFollowerRepository.saveAll(followers);
    }

    /**
     * Đếm số artist mà user đang follow.
     * Trả về 0 nếu userId không tồn tại (không throw exception).
     */
    public long countFollowingArtists(Long userId) {
        if (userId == null) return 0L;
        return artistFollowerRepository.countFollowingByUserId(userId);
    }

    /**
     * Lấy danh sách artist mà user đang follow, kèm follower count toàn cầu của từng artist.
     * Trả về list rỗng nếu userId null hoặc user chưa follow ai.
     */
    public List<FollowingArtistDto> getFollowingArtists(Long userId) {
        if (userId == null) return Collections.emptyList();
        return artistFollowerRepository.findFollowingArtistsByUserId(userId);
    }
}
