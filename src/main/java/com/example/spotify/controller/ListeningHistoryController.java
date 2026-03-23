package com.example.spotify.controller;


import com.example.spotify.dto.HistoryRequestDto;
import com.example.spotify.dto.TrackHistoryDto;
import com.example.spotify.service.HistoryRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class ListeningHistoryController {
    private final HistoryRequestService historyService;

    public ListeningHistoryController(HistoryRequestService historyService) {
        this.historyService = historyService;
    }

    @PostMapping("/played")
    public ResponseEntity<String> recordListeningHistory(@RequestBody HistoryRequestDto request) {
        if (request.getUserId() == null || request.getTrackId() == null) {
            return ResponseEntity.badRequest().body("Thiếu thông tin userId hoặc trackId");
        }
        try {
            historyService.saveListeningHistory(request.getUserId(), request.getTrackId());
            return ResponseEntity.ok("Lưu lịch sử nghe nhạc thành công");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi server: " + e.getMessage());
        }
    }

    @GetMapping("/played-all-time/{userId}")
    public ResponseEntity<List<TrackHistoryDto>> getPlayedHistory(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(historyService.getHistoryGrouped(userId));
    }
}
