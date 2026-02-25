package ru.practicum.ewm.stats.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ClientRequestDto {

    @NotNull(message = "Ошибка валидации. Дата начала не может быть пустой")
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull(message = "Ошибка валидации. Дата окончания не может быть пустой")
    @Future
    private LocalDateTime end;
    private List<String> uris;
    private Boolean unique;
}
