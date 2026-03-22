package com.example.spotify.repository;

import com.example.spotify.entity.ListeningHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRequestRepository extends JpaRepository<ListeningHistory, Integer> {

}
