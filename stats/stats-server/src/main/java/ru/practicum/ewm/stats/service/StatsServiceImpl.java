package ru.practicum.ewm.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.EndpointHit;
import ru.practicum.ewm.stats.EndpointHitMapper;
import ru.practicum.ewm.stats.StatsRepository;
import ru.practicum.ewm.stats.dto.HitEndpointDto;
import ru.practicum.ewm.stats.dto.StatsViewDto;
import ru.practicum.ewm.stats.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final EndpointHitMapper mapper;
    private final StatsRepository statsRepository;

    @Override
    public List<StatsViewDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new ValidationException("Окончание не может быть раньше старта");
        }
        return unique ? statsRepository.findUniqueStats(start, end, uris) : statsRepository.findStats(start, end, uris);
    }

    @Override
    @Transactional
    public HitEndpointDto addHit(HitEndpointDto hitEndpointDto) {
        EndpointHit saved = statsRepository.save(mapper.toEntity(hitEndpointDto));
        return mapper.toDto(saved);
    }
}
