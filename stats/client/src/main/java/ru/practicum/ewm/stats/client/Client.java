package ru.practicum.ewm.stats.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.stats.dto.ClientRequestDto;
import ru.practicum.ewm.stats.dto.DateTimeFormats;
import ru.practicum.ewm.stats.dto.HitEndpointDto;
import ru.practicum.ewm.stats.dto.StatsViewDto;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class Client {

    private static final Logger log = LoggerFactory.getLogger(Client.class);

    private final RestClient restClient;

    public Client(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<StatsViewDto> getStats(ClientRequestDto clientRequestDto) {
        if (clientRequestDto.getStart() == null || clientRequestDto.getEnd() == null) {
            throw new IllegalArgumentException("Даты начала и окончания должны быть заданы");
        }

        if (clientRequestDto.getEnd().isBefore(clientRequestDto.getStart())) {
            throw new IllegalArgumentException("Ошибка валидации. Дата окончания не может быть раньше даты начала");
        }

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromPath("/stats")
                .queryParam("start", clientRequestDto.getStart().format(DateTimeFormatter.ofPattern(DateTimeFormats.FORMATTER)))
                .queryParam("end", clientRequestDto.getEnd().format(DateTimeFormatter.ofPattern(DateTimeFormats.FORMATTER)));

        if (clientRequestDto.getUris() != null && !clientRequestDto.getUris().isEmpty()) {
            uriComponentsBuilder.queryParam("uris", clientRequestDto.getUris());
        }

        if (clientRequestDto.getUnique() != null) {
            uriComponentsBuilder.queryParam("unique", clientRequestDto.getUnique());
        }

        return restClient.get()
                .uri(uriComponentsBuilder.build().toUriString())
                .retrieve()
                .body(new ParameterizedTypeReference<List<StatsViewDto>>() {});
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
        }
    }
}