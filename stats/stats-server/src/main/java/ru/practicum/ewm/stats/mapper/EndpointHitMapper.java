package ru.practicum.ewm.stats;

import ru.practicum.ewm.stats.dto.HitEndpointDto;

public class EndpointHitMapper {

    public static ru.practicum.ewm.stats.EndpointHit toEntity(HitEndpointDto dto) {
        return new ru.practicum.ewm.stats.EndpointHit(
                dto.getApp(),
                dto.getUri(),
                dto.getIp(),
                dto.getTimestamp()
        );
    }

    public static HitEndpointDto toDto(ru.practicum.ewm.stats.EndpointHit entity) {
        return new HitEndpointDto(
                entity.getId(),
                entity.getApp(),
                entity.getUri(),
                entity.getIp(),
                entity.getTimestamp()
        );
    }
}
