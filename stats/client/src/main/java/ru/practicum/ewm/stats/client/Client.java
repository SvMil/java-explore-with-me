package ru.practicum.ewm.stats.client;

import lombok.Data;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.stats.dto.HitEndpointDto;
import ru.practicum.ewm.stats.dto.StatsViewDto;
import ru.practicum.ewm.stats.dto.DateTimeFormats;
import ru.practicum.ewm.stats.dto.ClientRequestDto;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.time.format.DateTimeFormatter;

@Data
public class Client {
    private RestClient restClient;

    public Client(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<StatsViewDto> getStats(ClientRequestDto clientRequestDto) {

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