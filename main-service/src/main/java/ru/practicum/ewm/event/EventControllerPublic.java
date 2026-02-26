package ru.practicum.ewm.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.event.dto.FullEventDto;
import ru.practicum.ewm.event.service.EventService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EvenSearchParamsAll;

import java.util.List;
import java.time.LocalDateTime;

@RequestMapping("events")
@RestController
@RequiredArgsConstructor
public class EventControllerPublic {
    private final EventService service;

    @GetMapping("/{id}")
    public FullEventDto getEventById(
            @PathVariable long id,
            HttpServletRequest request
    ) {
        return service.getPublicEventById(id, request);
    }

    @GetMapping
    public List<EventShortDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        List<EventShortDto> result = service.getPublicEvents(new EvenSearchParamsAll(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size
        ), request);

        return result;
    }
}
