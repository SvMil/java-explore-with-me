package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.dto.EventUpdateStatusRequest;
import ru.practicum.ewm.event.dto.EventUpdateStatusResult;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.enums.RequestStatus;
import ru.practicum.ewm.enums.RequestStatusUpdateAction;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.dto.RequestParticipationDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final RequestMapper mapper;
    private final UserRepository userRepository;



    @Override
    public List<RequestParticipationDto> getRequests(Long userId) {
        findUserById(userId);

        return requestRepository.findByRequesterId(userId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<RequestParticipationDto> getEventRequestsForInitiator(Long userId, Long eventId) {
        Event event = findEventById(eventId);

        validateAccessToEvent(event, userId);

        return requestRepository.findByEventId(eventId).stream()
                .map(mapper::toDto)
                .toList();
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с заданным id не найдено"));
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с заданным id не найден"));
    }


    private Request findRequestById(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с заданным id не найден"));
    }

    @Override
    @Transactional
    public RequestParticipationDto create(Long userId, Long eventId) {
        User user = findUserById(userId);

        Event event = findEventById(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Пользователь уже организует мероприятие");
        }

        if (requestRepository.findByEventIdAndRequesterId(eventId, userId).isPresent()) {
            throw new ConflictException("Заявка уже подана");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Событие должно быть опубликовано для подачи запроса");
        }

        long confirmedRequests = findConfirmedRequests(eventId);

        if (event.getParticipantLimit() > 0 && confirmedRequests >= event.getParticipantLimit()) {
            throw new ConflictException("Достигнут лимит участников мероприятия");
        }

        Request request = new Request();
        request.setRequester(user);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now());

        if (isRequestModerationDisabled(event)) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        return mapper.toDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public EventUpdateStatusResult updateEventRequestStatus(
            Long userId,
            Long eventId,
            EventUpdateStatusRequest dto
    ) {
        Event event = findEventById(eventId);
        validateAccessToEvent(event, userId);

        if (!event.getRequestModeration()) {
            throw new ConflictException("Модерация отключена для данного мероприятия");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Событие не опубликовано");
        }

        List<Request> requests = requestRepository.findAllById(dto.getRequestIds());

        for (Request request : requests) {
            if (!request.getEvent().getId().equals(eventId)) {
                throw new NotFoundException("Запрос не принадлежит этому событию");
            }

            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConflictException("Обновлять можно только запросы, ожидающие обработки");
            }
        }

        long confirmedRequests = findConfirmedRequests(eventId);

        if (dto.getStatus() == RequestStatusUpdateAction.CONFIRMED) {
            if (event.getParticipantLimit() > 0 && confirmedRequests >= event.getParticipantLimit()) {
                throw new ConflictException("Достигнут лимит участников события");
            }
        }

        List<RequestParticipationDto> confirmed = new ArrayList<>();
        List<RequestParticipationDto> rejected = new ArrayList<>();

        for (Request request : requests) {

            if (dto.getStatus() == RequestStatusUpdateAction.CONFIRMED) {
                if (event.getParticipantLimit() > 0 && confirmedRequests >= event.getParticipantLimit()) {
                    request.setStatus(RequestStatus.REJECTED);
                    rejected.add(mapper.toDto(request));
//                    throw new ConflictException("Достигнут лимит участников события");
                } else {
                    request.setStatus(RequestStatus.CONFIRMED);
                    confirmed.add(mapper.toDto(request));
                    confirmedRequests++;
                }
            } else {
                request.setStatus(RequestStatus.REJECTED);
                rejected.add(mapper.toDto(request));
            }
        }

        requestRepository.saveAll(requests);

        return new EventUpdateStatusResult(confirmed, rejected);
    }

    @Override
    @Transactional
    public RequestParticipationDto cancel(Long userId, Long requestId) {
        Request request = findRequestById(requestId);

        if (!request.getRequester().getId().equals(userId)) {
            throw new ConflictException("Нельзя отменить запрос другого пользователя");
        }

        request.setStatus(RequestStatus.CANCELED);

        return mapper.toDto(requestRepository.save(request));
    }

    private boolean isRequestModerationDisabled(Event event) {
        return !event.getRequestModeration() || event.getParticipantLimit() == 0;
    }

    private long findConfirmedRequests(Long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    private void validateAccessToEvent(Event event, Long userId) {
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("Выполнение операции доступно только организатору");
        }
    }
}
