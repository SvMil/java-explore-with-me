package ru.practicum.ewm.stats.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.stats.dto.HitEndpointDto;
import ru.practicum.ewm.stats.dto.StatsViewDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Client {

    private static final Logger log = LoggerFactory.getLogger(Client.class);

    private final RestClient restClient;

    public Client(RestClient restClient) {
        this.restClient = restClient;
    }

    public void saveHit(HitEndpointDto endpointHitDto) {
        try {
            restClient.post().uri("/hit")
                    .body(endpointHitDto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (ResourceAccessException e) {
            log.warn("Сервис статистики недоступен: {}", e.getMessage());
        } catch (RestClientException e) {
            log.error("Ошибка при отправке статистики: {}", e.getMessage());
//        restClient.post().uri("/hit")
//                    .body(endpointHitDto)
//                    .retrieve()
//                    .toBodilessEntity();
        }
    }

    public List<StatsViewDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Даты начала и окончания должны быть заданы");
        }

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Даты окончания должна быть позже даты начала");
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
                .uri(uriComponentsBuilder.build().toUriString())
                .retrieve()
                .body(new ParameterizedTypeReference<List<StatsViewDto>>() {});
    }
}