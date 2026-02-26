package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface EventService {

    List<FullEventDto> getAdminEvents(EventSearchParamsAdmin params);

    FullEventDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto);

    FullEventDto getUserEventById(Long userId, Long eventId);

    FullEventDto getPublicEventById(Long eventId, HttpServletRequest request);

    List<EventShortDto> getUserEvents(Long userId, int from, int size);

    List<EventShortDto> getPublicEvents(EvenSearchParamsAll params, HttpServletRequest request);

    FullEventDto create(Long userId, CreateEventDto dto);

    FullEventDto update(Long userId, Long eventId, UpdateEventUserRequest dto);

}
