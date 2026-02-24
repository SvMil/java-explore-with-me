package ru.practicum.ewm.stats.client;

import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.stats.dto.HitEndpointDto;
import ru.practicum.ewm.stats.dto.StatsViewDto;
import ru.practicum.ewm.stats.dto.DateTimeFormats;
import org.springframework.core.ParameterizedTypeReference;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class Client {
    private RestClient restClient;

    public List<StatsViewDto> getStats(ClientRequestDto сlientRequestDto) {

        if (сlientRequestDto.getEnd().isBefore(сlientRequestDto.getStart())) {
            throw new IllegalArgumentException("Ошибка валидации. Дата окончания не может быть раньше даты начала");
        }

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromPath("/stats")
                .queryParam("start", сlientRequestDto.getStart().format(DateTimeFormatter.ofPattern(DateTimeFormats.FORMATTER)))
                .queryParam("end", сlientRequestDto.getEnd().format(DateTimeFormatter.ofPattern(DateTimeFormats.FORMATTER)));

        if (сlientRequestDto.getUris() != null && !сlientRequestDto.getUris().isEmpty()) {
            uriComponentsBuilder.queryParam("uris", сlientRequestDto.getUris());
        }

        if (сlientRequestDto.getUnique() != null) {
            uriComponentsBuilder.queryParam("unique", сlientRequestDto.getUnique());
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