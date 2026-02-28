package ru.practicum.ewm.event;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import ru.practicum.ewm.request.dto.RequestParticipationDto;
import ru.practicum.ewm.request.service.RequestService;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/users/{userId}/events")
@RestController
@RequiredArgsConstructor
public class EventControllerPrivate {
    private final RequestService requestService;
    private final EventService eventService;

    @GetMapping("/{eventId}")
    public FullEventDto getEventById(
            @PathVariable long userId,
            @PathVariable long eventId
    ) {
        return eventService.getUserEventById(userId, eventId);
    }

    @GetMapping
    public List<EventShortDto> getEvents(
            @PathVariable long userId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        return eventService.getUserEvents(userId, from, size);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestParticipationDto> getEventRequests(
            @PathVariable long userId,
            @PathVariable long eventId
    ) {
        return requestService.getEventRequestsForInitiator(userId, eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FullEventDto addEvent(
            @PathVariable long userId,
            @RequestBody @Valid CreateEventDto dto
    ) {
        return eventService.create(userId, dto);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public FullEventDto updateEvent(
            @PathVariable long userId,
            @PathVariable long eventId,
            @RequestBody @Valid UpdateEventUserRequest dto
    ) {
        return eventService.update(userId, eventId, dto);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventUpdateStatusResult updateStatus(
            @PathVariable long userId,
            @PathVariable long eventId,
            @RequestBody @Valid EventUpdateStatusRequest dto
    ) {
        return requestService.updateEventRequestStatus(userId, eventId, dto);
    }
}
