package com.example.spotify.dto;

import java.util.List;

public class TrackSearchResponseDto {
    private List<TrackSearchItemDto> items;
    private int page;
    private int size;
    private long total;

    public TrackSearchResponseDto(List<TrackSearchItemDto> items, int page, int size, long total) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.total = total;
    }

    public List<TrackSearchItemDto> getItems() { return items; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotal() { return total; }

    public void setItems(List<TrackSearchItemDto> items) { this.items = items; }
    public void setPage(int page) { this.page = page; }
    public void setSize(int size) { this.size = size; }
    public void setTotal(long total) { this.total = total; }
}
