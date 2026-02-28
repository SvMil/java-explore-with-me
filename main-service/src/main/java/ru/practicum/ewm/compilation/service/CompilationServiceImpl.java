package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.compilation.dto.CreateCompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;


import java.util.List;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper mapper;

    @Override
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        List<Compilation> compilations;

        if (pinned == null) {
            compilations = compilationRepository.findAll(pageable).getContent();
        } else {
            compilations = compilationRepository.findByPinned(pinned, pageable).getContent();
        }

        return compilations.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public CompilationDto getById(Long id) {
        return mapper.toDto(findById(id));
    }

    private Compilation findById(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Подборка с таким id не найдена"));
    }

    @Override
    @Transactional
    public CompilationDto create(CreateCompilationDto dto) {
        Set<Event> events = new HashSet<>();

        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            events.addAll(eventRepository.findAllByIdIn(dto.getEvents()));
        }

        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned() != null ? dto.getPinned() : false);
        compilation.setEvents(events);

        return mapper.toDto(compilationRepository.save(compilation));

    }

    @Override
    @Transactional
    public CompilationDto update(Long id, UpdateCompilationDto dto) {
        Compilation compilation = findById(id);

        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }

        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }

        if (dto.getEvents() != null) {
            Set<Event> newEvents = new HashSet<>(eventRepository.findAllByIdIn(dto.getEvents()));
            compilation.setEvents(newEvents);
        }

        return mapper.toDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        compilationRepository.delete(findById(id));
    }
}
