package ru.practicum.ewm.request;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import ru.practicum.ewm.request.service.RequestService;
import ru.practicum.ewm.request.dto.RequestParticipationDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateRequestController {
    private final RequestService service;

    @GetMapping
    public List<RequestParticipationDto> getRequests(
            @PathVariable long userId
    ) {
        return service.getRequests(userId);
    }


    @PatchMapping("/{requestId}/cancel")
    public RequestParticipationDto cancelRequest(
            @PathVariable long userId,
            @PathVariable long requestId
    ) {
        return service.cancel(userId, requestId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestParticipationDto addRequest(
            @PathVariable long userId,
            @RequestParam long eventId
    ) {
        return service.create(userId, eventId);
    }
}
