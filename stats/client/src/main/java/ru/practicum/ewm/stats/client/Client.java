package ru.practicum.ewm.stats.client;

import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.stats.dto.HitEndpointDto;
import ru.practicum.ewm.stats.dto.StatsViewDto;
import org.springframework.core.ParameterizedTypeReference;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class Client {
    private RestClient restClient;

    public List<StatsViewDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Ошибка валидации. Даты начала и окончания должны не могут быть пустыми");
        }

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Ошибка валидации. Дата окончания не может быть раньше даты начала");
        }

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromPath("/stats")
                .queryParam("start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .queryParam("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        if (uris != null && !uris.isEmpty()) {
            uriComponentsBuilder.queryParam("uris", uris);
        }

        if (unique != null) {
            uriComponentsBuilder.queryParam("unique", unique);
        }

        return restClient.get()
                .uri(uriComponentsBuilder.build().toUri())
                .retrieve()
                .body(new ParameterizedTypeReference<List<StatsViewDto>>() {});
    }

    public void addHit(HitEndpointDto hitEndpointDto) {
        restClient.post().uri("/hit")
                .body(hitEndpointDto)
                .retrieve()
                .toBodilessEntity();
    }
}