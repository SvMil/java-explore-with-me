package ru.practicum.ewm.event.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.category.dto.CategoryDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
    private String annotation;
    private CategoryDto category;
    private Long confirmedRequests;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Long id;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
}
