package ru.practicum.ewm.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.enums.EventSort;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.enums.RequestStatus;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.stats.client.Client;
import ru.practicum.ewm.stats.dto.ClientRequestDto;
import ru.practicum.ewm.stats.dto.HitEndpointDto;
import ru.practicum.ewm.stats.dto.StatsViewDto;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final Client statsClient;
    private final UserRepository userRepository;
    private final EventMapper mapper;

    @Override
    public List<FullEventDto> getAdminEvents(EventSearchParamsAdmin params) {
        Pageable pageable = PageRequest.of(
                params.getFrom() / params.getSize(),
                params.getSize()
        );

        List<Long> categories = params.getCategories();
        if (categories != null && categories.isEmpty()) {
            categories = null;
        }

        List<EventState> states = params.getStates() == null
                ? null
                : params.getStates().stream()
                .map(EventState::valueOf)
                .toList();


        List<Event> events = eventRepository.findAllByAdminFilters(
                params.getUsers(),
                states,
                categories,
                params.getRangeStart(),
                params.getRangeEnd(),
                pageable
        );

        Map<Long, Long> views = getViews(events);

        return events.stream()
                .map(event -> {
                    FullEventDto fullDto = mapper.toFullDto(event, views.getOrDefault(event.getId(), 0L));
                    fullDto.setConfirmedRequests(getConfirmedRequests(event.getId()));
                    return fullDto;
                })
                .toList();
    }

    @Override
    @Transactional
    public FullEventDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto) {
        Event event = findEventById(eventId);

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case REJECT_EVENT:
                    validatePending(event, "Отклонить можно только мероприятия в статусе ожидания");
                    event.setState(EventState.CANCELED);
                    break;

                case PUBLISH_EVENT:
                    validatePending(event, "Публикация доступна только для мероприятий в статусе ожидания");
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;

                default:
                    break;
            }
        }

        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }

        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }


        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }

        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }

        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }

        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }

        if (dto.getCategory() != null) {
            event.setCategory(findCategoryById(dto.getCategory()));
        }

        Event saved = eventRepository.save(event);

        long views = getViews(List.of(saved))
                .getOrDefault(saved.getId(), 0L);

        FullEventDto fullDto = mapper.toFullDto(saved, views);
        fullDto.setConfirmedRequests(getConfirmedRequests(eventId));
        return fullDto;
    }

    @Override
    public FullEventDto getUserEventById(Long userId, Long eventId) {
        Event event = findEventById(eventId);

        validateAccess(event, userId);

        long views = getViews(List.of(event))
                .getOrDefault(eventId, 0L);

        FullEventDto fullDto = mapper.toFullDto(event, views);
        fullDto.setConfirmedRequests(getConfirmedRequests(eventId));
        return fullDto;
    }

    @Override
    public FullEventDto getPublicEventById(Long eventId, HttpServletRequest request) {
        logHit(request);

        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Мероприятие с заданным Id не найдено"));

        Map<Long, Long> views = getViews(List.of(event));

        FullEventDto fullDto = mapper.toFullDto(event, views.getOrDefault(eventId, 0L));
        fullDto.setConfirmedRequests(getConfirmedRequests(eventId));

        return fullDto;
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);

        Map<Long, Long> views = getViews(events);

        return events.stream()
                .map(event -> {
                    EventShortDto shortDto = mapper.toShortDto(event, views.getOrDefault(event.getId(), 0L));
                    shortDto.setConfirmedRequests(getConfirmedRequests(event.getId()));
                    return shortDto;
                })
                .toList();
    }


    @Override
    public List<EventShortDto> getPublicEvents(EvenSearchParamsAll params, HttpServletRequest request) {
        log.info("Started getPublicEvents with params={}", params);

        try {
            logHit(request);
        } catch (Exception e) {
            log.warn("Ошибка сохранения статистики");
        }

        if (params.getRangeStart() != null
                && params.getRangeEnd() != null
                && params.getRangeStart().isAfter(params.getRangeEnd())
        ) {
            throw new ValidationException("Дата начала не может быть позднее даты окончания");
        }

        int size = params.getSize();
        int from = params.getFrom();

        if (size <= 0) {
            size = 10;
        }

        if (from < 0) {
            from = 10;
        }

        Pageable pageable = PageRequest.of(from / size, size);

        List<Long> categories = params.getCategories();
        if (categories != null && categories.isEmpty()) {
            log.debug("Categories list is empty, converting to null");
            categories = null;
        }

        log.debug(
                "Calling repository with filters: text={}, categories={}, paid={}, " +
                        "rangeStart={}, rangeEnd={}, onlyAvailable={}, pageable={}",
                params.getText(),
                categories,
                params.getPaid(),
                params.getRangeStart(),
                params.getRangeEnd(),
                params.getOnlyAvailable(),
                pageable
        );

        List<Event> events;

        if (categories == null || categories.isEmpty()) {
            events = eventRepository.findAllByPublicFiltersWithoutCategories(
                    params.getText(),
                    params.getPaid(),
                    params.getRangeStart(),
                    params.getRangeEnd(),
                    "PUBLISHED",
                    pageable
            );
        } else {
            events = eventRepository.findAllByPublicFiltersWithCategories(
                    params.getText(),
                    categories,
                    params.getPaid(),
                    params.getRangeStart(),
                    params.getRangeEnd(),
                    "PUBLISHED",
                    pageable
            );
        }

        log.info("Repository returned {} events", events.size());

        if (params.getOnlyAvailable() != null && params.getOnlyAvailable()) {
            int before = events.size();
            events = events.stream()
                    .filter(event ->
                            event.getParticipantLimit() == 0 || getConfirmedRequests(event.getId())
                                    < event.getParticipantLimit())
                    .toList();

            log.debug("onlyAvailable filter applied: before={}, after={}", before, events.size());
        }

        Map<Long, Long> views = getViews(events);
        log.debug("Views loaded for {} events", views.size());

        Stream<Event> stream = events.stream();

        EventSort eventSort;

        if (params.getSort() == null || params.getSort().isBlank()) {
            eventSort = EventSort.EVENT_DATE;
        } else {
            try {
                eventSort = EventSort.valueOf(params.getSort());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid sort parameter: {}", params.getSort());
                throw new ValidationException("Указан некорректный вариант сортировки");
            }
        }

        log.warn("Sorting events by {}", eventSort);

        switch (eventSort) {
            case EVENT_DATE:
                stream = stream.sorted(
                        Comparator.comparing(Event::getEventDate)
                );
                break;
            case VIEWS:
                stream = stream.sorted(
                        Comparator.comparing(
                                event -> views.getOrDefault(event.getId(), 0L),
                                Comparator.reverseOrder()
                        )
                );
                break;
            default:
                throw new ValidationException("Задано некорректное значение сортировки");
        }

        List<EventShortDto> result = stream
                .map(event -> {
                    EventShortDto shortDto = mapper.toShortDto(event, views.getOrDefault(event.getId(), 0L));
                    shortDto.setConfirmedRequests(getConfirmedRequests(event.getId()));
                    return shortDto;
                })
                .toList();

        log.info("getPublicEvents finished, returning {} items", result.size());
        return result;
    }

    private void validateAccess(Event event, Long userId) {
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("Обновление мероприятия доступно только организатору");
        }
    }

    private void validatePending(Event event, String message) {
        if (event.getState() != EventState.PENDING) {
            throw new ConflictException(message);
        }
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Начало мероприятия не может быть ранее двух часов 2 часа с текущего момента");
        }
    }

    @Override
    @Transactional
    public FullEventDto create(Long userId, CreateEventDto dto) {
        User user = findUserById(userId);
        Category category = findCategoryById(dto.getCategory());

        validateEventDate(dto.getEventDate());
        Event event = mapper.toEntity(dto);
        event.setCategory(category);
        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        FullEventDto fullDto = mapper.toFullDto(eventRepository.save(event), 0L);
        fullDto.setConfirmedRequests(0L);

        return fullDto;
    }

    @Override
    @Transactional
    public FullEventDto update(Long userId, Long eventId, UpdateEventUserRequest dto) {
        Event event = findEventById(eventId);
        validateAccess(event, userId);

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Запрещено обновлять мероприятия, которые уже опубликованы");
        }

        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }

        if (dto.getEventDate() != null) {
            validateEventDate(dto.getEventDate());
            event.setEventDate(dto.getEventDate());
        }

        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }

        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }

        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }

        if (dto.getCategory() != null) {
            event.setCategory(findCategoryById(dto.getCategory()));
        }

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    break;
            }
        }

        Event saved = eventRepository.save(event);

        long views = getViews(List.of(saved))
                .getOrDefault(saved.getId(), 0L);

        FullEventDto fullDto = mapper.toFullDto(saved, views);
        fullDto.setConfirmedRequests(getConfirmedRequests(eventId));
        return fullDto;
    }

    private Event findEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Мероприятие с указанным id не найдено"));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с указанным id не найден"));
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория c указанным id не найдена"));
    }

    private void logHit(HttpServletRequest request) {
        HitEndpointDto hit = new HitEndpointDto();
        hit.setTimestamp(LocalDateTime.now());
        hit.setIp(request.getRemoteAddr());
        hit.setApp("ewm-main-service");
        hit.setUri(request.getRequestURI());
        statsClient.addHit(hit);
    }

    private long getConfirmedRequests(Long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    private Map<Long, Long> getViews(List<Event> events) {
        if (events.isEmpty()) {
            return Map.of();
        }

        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .toList();

        LocalDateTime start = events.stream()
                .map(event -> {
                    if (event.getPublishedOn() != null) {
                        return event.getPublishedOn();
                    } else if (event.getState() == EventState.PUBLISHED) {
                        return event.getCreatedOn();
                    } else {
                        return LocalDateTime.now();
                    }
                })
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        try {
            ClientRequestDto clientReques = new ClientRequestDto();
            clientReques.setUris(uris);
            clientReques.setUnique(true);
            clientReques.setStart(start);
            clientReques.setEnd(LocalDateTime.now());
            List<StatsViewDto> stats = statsClient.getStats(clientReques);

            return stats.stream()
                    .collect(Collectors.toMap(
                            stat -> Long.parseLong(stat.getUri().substring("/events/".length())),
                            StatsViewDto::getHits
                    ));
        } catch (Exception e) {
            log.warn("Ошибка получения статистики", e);
            return Collections.emptyMap();
        }
    }
}
