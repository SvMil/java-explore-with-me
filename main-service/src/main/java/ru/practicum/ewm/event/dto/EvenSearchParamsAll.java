package ru.practicum.ewm.event.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvenSearchParamsAll {

    private String text;
    private List<Long> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable = false;
    private String sort;
    private int from = 0;
    private int size = 10;
}
