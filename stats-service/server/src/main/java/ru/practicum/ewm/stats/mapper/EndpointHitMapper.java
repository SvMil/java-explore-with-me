package ru.practicum.ewm.stats;

import ru.practicum.ewm.stats.dto.HitEndpointDto;
import org.springframework.stereotype.Component;

@Component
public class EndpointHitMapper {

    public EndpointHit toEntity(HitEndpointDto dto) {
        return new EndpointHit(
                dto.getApp(),
                dto.getUri(),
                dto.getIp(),
                dto.getTimestamp()
        );
    }

    public HitEndpointDto toDto(EndpointHit entity) {
        return new HitEndpointDto(
                entity.getId(),
                entity.getApp(),
                entity.getUri(),
                entity.getIp(),
                entity.getTimestamp()
        );
    }
}
