package ru.practicum.ewm.event.dto;

import jakarta.validation.constraints.*;
import ru.practicum.ewm.location.dto.LocationDto;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;


import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventDto {

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private LocationDto location;

    private Boolean paid = false;

    @PositiveOrZero
    private int participantLimit = 0;

    private Boolean requestModeration = true;

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
}
