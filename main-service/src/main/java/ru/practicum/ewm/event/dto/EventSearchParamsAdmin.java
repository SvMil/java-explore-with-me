package ru.practicum.ewm.event.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventSearchParamsAdmin {

    private List<Long> users;
    private List<String> states;
    private List<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private int from = 0;
    private int size = 10;
}
