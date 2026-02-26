package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.EventSearchParamsAdmin;
import ru.practicum.ewm.event.dto.FullEventDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDateTime;

@RequestMapping("/admin/events")
@RestController
@RequiredArgsConstructor
public class EventControllerAdmin {
    private final EventService service;

    @PatchMapping("/{eventId}")
    public FullEventDto updateEvent(
            @PathVariable long eventId,
            @RequestBody @Valid UpdateEventAdminRequest dto
    ) {
        return service.updateEventByAdmin(eventId, dto);
    }

    @GetMapping
    public List<FullEventDto> getEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getAdminEvents(new EventSearchParamsAdmin(
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                from,
                size
        ));
    }
}
