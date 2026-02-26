package ru.practicum.ewm.request.service;

import ru.practicum.ewm.event.dto.EventUpdateStatusResult;
import ru.practicum.ewm.request.dto.RequestParticipationDto;
import ru.practicum.ewm.event.dto.EventUpdateStatusRequest;

import java.util.List;

public interface RequestService {

    List<RequestParticipationDto> getRequests(Long userId);

    List<RequestParticipationDto> getEventRequestsForInitiator(Long userId, Long eventId);

    RequestParticipationDto create(Long userId, Long eventId);

    EventUpdateStatusResult updateEventRequestStatus(
            Long userId,
            Long eventId,
            EventUpdateStatusRequest dto
    );

    RequestParticipationDto cancel(Long userId, Long requestId);
}
