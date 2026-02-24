package ru.practicum.ewm.stats.service;

import ru.practicum.ewm.stats.dto.HitEndpointDto;
import ru.practicum.ewm.stats.dto.StatsViewDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    List<StatsViewDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

    HitEndpointDto addHit(HitEndpointDto HitEndpointDto);
}
